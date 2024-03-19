package io.schiar.fridgnet.model.repository

import io.schiar.fridgnet.Log
import io.schiar.fridgnet.model.Address
import io.schiar.fridgnet.model.AddressLocationsGeoLocations
import io.schiar.fridgnet.model.AdministrativeLevel
import io.schiar.fridgnet.model.AdministrativeLevel.CITY
import io.schiar.fridgnet.model.GeoLocation
import io.schiar.fridgnet.model.ImageAddress
import io.schiar.fridgnet.model.Location
import io.schiar.fridgnet.model.LocationGeoLocation
import io.schiar.fridgnet.model.datasource.AddressDataSource
import io.schiar.fridgnet.model.datasource.ImageDataSource
import io.schiar.fridgnet.model.datasource.LocationDataSource
import io.schiar.fridgnet.model.datasource.local.CurrentLocationGeoLocationDataSource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Collections.synchronizedMap as syncMapOf
import java.util.Collections.synchronizedSet as syncSetOf

class HomeRepository(
    private val addressDataSource: AddressDataSource,
    private val locationDataSource: LocationDataSource,
    private val imageDataSource: ImageDataSource,
    private val currentLocationGeoLocationDataSource : CurrentLocationGeoLocationDataSource,
    private val externalScope: CoroutineScope
) {
    private var _currentAdministrativeLevel = CITY
    private val _administrativeLevels = AdministrativeLevel.entries
    private val _currentAdministrativeLevelStateFlow = MutableStateFlow(_currentAdministrativeLevel)
    private var _currentLocationGeoLocations: List<LocationGeoLocation> = emptyList()

    val administrativeLevels: Flow<List<AdministrativeLevel>> = MutableStateFlow(_administrativeLevels)
    val currentAdministrativeLevel: Flow<AdministrativeLevel> = _currentAdministrativeLevelStateFlow

    private val geoLocationRetrievingAddressSet = syncSetOf(mutableSetOf<GeoLocation>())
    private val addressRetrievingLocationSet = syncSetOf(mutableSetOf<String>())

    private val administrativeLevelAddressNameLocationGeoLocation = run {
        syncMapOf(_administrativeLevels.associateWith { mutableMapOf<String, LocationGeoLocation>() })
    }

    val locationGeoLocations = merge(
        imageDataSource.retrieveWithAddress().onEach { it.forEach(::onEachImageAddress) },
        addressDataSource.retrieve().onEach { it.forEach(::onEachAddressLocationsGeoLocations) },
        locationDataSource.retrieve().onEach { it.forEach(::onEachLocation) },
        _currentAdministrativeLevelStateFlow.onEach { _currentAdministrativeLevel = it }
    ).map {
        _currentLocationGeoLocations = administrativeLevelAddressNameLocationGeoLocation[
            _currentAdministrativeLevel
        ]?.values?.toList() ?: emptyList()
        _currentLocationGeoLocations
    }

    private fun onEachImageAddress(imageAddress: ImageAddress) {
        val (image, addressRetrieved) = imageAddress
        val geoLocation = image.geoLocation
        if (addressRetrieved != null) {
            assignNewLocationGeoLocation(
                administrativeLevel = CITY,
                addressName = addressRetrieved.name(),
                initialGeoLocation = geoLocation
            )
        }
        retrieveAddressForGeoLocation(
            geoLocation = geoLocation,
            addressFromGeoLocation = addressRetrieved
        )
    }

    private fun onEachAddressLocationsGeoLocations(
        addressLocationsGeoLocations: AddressLocationsGeoLocations
    ) {
        val (address, geoLocations, administrativeLevelLocation) = addressLocationsGeoLocations
        val locationsRetrieved = administrativeLevelLocation.values
        assignNewLocationGeoLocation(
            administrativeLevel = CITY,
            addressName = address.name(),
            location = administrativeLevelLocation[CITY],
            initialGeoLocation = geoLocations[0]
        )
        retrieveLocationsForAddress(address = address, locationsFromAddress = locationsRetrieved)
    }

    private fun onEachLocation(location: Location) {
        assignNewLocationGeoLocation(
            administrativeLevel = location.administrativeLevel,
            addressName = location.addressName(),
            location = location,
            initialGeoLocation = location.boundingBox.center()
        )
    }

    fun selectLocationGeoLocationAt(index: Int) {
        val locationGeoLocation = _currentLocationGeoLocations[index]
        log(
            method = "selectLocationGeoLocationAt",
            msg = "LocationGeoLocation at $index is $locationGeoLocation"
        )
        currentLocationGeoLocationDataSource.update(locationGeoLocation = locationGeoLocation)
    }

    fun changeCurrentAdministrativeLevel(index: Int) {
        _currentAdministrativeLevelStateFlow.update { _administrativeLevels[index] }
    }

    suspend fun removeAllImages() { imageDataSource.delete() }

    private fun retrieveAddressForGeoLocation(
        geoLocation: GeoLocation, addressFromGeoLocation: Address?
    ) {
        if (addressFromGeoLocation != null) geoLocationRetrievingAddressSet.add(geoLocation)
        val addressNotBeingRetrieved = !geoLocationRetrievingAddressSet.contains(geoLocation)
        if (addressNotBeingRetrieved) {
            geoLocationRetrievingAddressSet.add(element = geoLocation)
            log(method = "retrieveAddressForGeoLocation", msg = "Retrieve Address for $geoLocation")
            externalScope.launch { addressDataSource.retrieveAddressFor(geoLocation = geoLocation) }
        }
    }

    private fun retrieveLocationsForAddress(
        address: Address, locationsFromAddress: Collection<Location>
    ) {
        addressRetrievingLocationSet.addAll(locationsFromAddress.map { it.addressName() })

        val administrativeLevelsWithNonRetrievedLocation = _administrativeLevels.filter {
                administrativeLevel -> run {
                val addressName = address.name(administrativeLevel = administrativeLevel)
                !addressRetrievingLocationSet.contains(addressName)
            }
        }

        for (administrativeLevel in administrativeLevelsWithNonRetrievedLocation) {
            val addressName = address.name(administrativeLevel = administrativeLevel)
            addressRetrievingLocationSet.add(addressName)
            externalScope.launch {
                log(
                    method = "retrieveLocationForAddress",
                    msg = "Retrieve Location for $addressName"
                )
                locationDataSource.retrieveLocationFor(
                    address = address,
                    administrativeLevel = administrativeLevel
                )
            }
        }
    }

    private fun assignNewLocationGeoLocation(
        administrativeLevel: AdministrativeLevel,
        addressName: String,
        location: Location? = null,
        initialGeoLocation: GeoLocation
    ) {
        val locationGeoLocation = (administrativeLevelAddressNameLocationGeoLocation[
            administrativeLevel
        ] ?: return)[addressName]
        val needToAssignNewLocationGeoLocation = locationGeoLocation == null ||
                (locationGeoLocation.location == null && location != null)

        if (needToAssignNewLocationGeoLocation) {
            val newLocationGeoLocation = LocationGeoLocation(
                location = location,
                initialGeoLocation = initialGeoLocation
            )
            log(
                method = "assignNewLocationGeoLocation",
                msg = "Assign $newLocationGeoLocation " +
                        "to administrativeLevelAddressNameLocationGeoLocation[" +
                        "$administrativeLevel" +
                       "][$addressName]"
            )
            administrativeLevelAddressNameLocationGeoLocation[
                administrativeLevel
            ]?.set(addressName, newLocationGeoLocation)
        }
    }

    private fun log(method: String, msg: String) {
        Log.d(tag = "HomeRepository.$method", msg = msg)
    }
}