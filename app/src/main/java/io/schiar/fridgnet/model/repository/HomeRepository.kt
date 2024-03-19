package io.schiar.fridgnet.model.repository

import io.schiar.fridgnet.Log
import io.schiar.fridgnet.model.Address
import io.schiar.fridgnet.model.AddressLocationsGeoLocations
import io.schiar.fridgnet.model.AdministrativeUnit
import io.schiar.fridgnet.model.AdministrativeUnit.CITY
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
    private var _currentAdministrativeUnit = CITY
    private val _administrativeUnits = AdministrativeUnit.entries
    private val _currentAdministrativeUnitStateFlow = MutableStateFlow(_currentAdministrativeUnit)
    private var _currentLocationGeoLocations: List<LocationGeoLocation> = emptyList()

    val administrativeUnits: Flow<List<AdministrativeUnit>> = MutableStateFlow(_administrativeUnits)
    val currentAdministrativeUnit: Flow<AdministrativeUnit> = _currentAdministrativeUnitStateFlow

    private val geoLocationRetrievingAddressSet = syncSetOf(mutableSetOf<GeoLocation>())
    private val addressRetrievingLocationSet = syncSetOf(mutableSetOf<String>())

    private val administrativeUnitAddressNameLocationGeoLocation = run {
        syncMapOf(_administrativeUnits.associateWith { mutableMapOf<String, LocationGeoLocation>() })
    }

    val locationGeoLocations = merge(
        imageDataSource.retrieveWithAddress().onEach { it.forEach(::onEachImageAddress) },
        addressDataSource.retrieve().onEach { it.forEach(::onEachAddressLocationsGeoLocations) },
        locationDataSource.retrieve().onEach { it.forEach(::onEachLocation) },
        _currentAdministrativeUnitStateFlow.onEach { _currentAdministrativeUnit = it }
    ).map {
        _currentLocationGeoLocations = administrativeUnitAddressNameLocationGeoLocation[
            _currentAdministrativeUnit
        ]?.values?.toList() ?: emptyList()
        _currentLocationGeoLocations
    }

    private fun onEachImageAddress(imageAddress: ImageAddress) {
        val (image, addressRetrieved) = imageAddress
        val geoLocation = image.geoLocation
        if (addressRetrieved != null) {
            assignNewLocationGeoLocation(
                administrativeUnit = CITY,
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
        val (address, geoLocations, administrativeUnitLocation) = addressLocationsGeoLocations
        val locationsRetrieved = administrativeUnitLocation.values
        assignNewLocationGeoLocation(
            administrativeUnit = CITY,
            addressName = address.name(),
            location = administrativeUnitLocation[CITY],
            initialGeoLocation = geoLocations[0]
        )
        retrieveLocationsForAddress(address = address, locationsFromAddress = locationsRetrieved)
    }

    private fun onEachLocation(location: Location) {
        assignNewLocationGeoLocation(
            administrativeUnit = location.administrativeUnit,
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

    fun changeCurrentAdministrativeUnit(index: Int) {
        _currentAdministrativeUnitStateFlow.update { _administrativeUnits[index] }
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

        val administrativeUnitsWithNonRetrievedLocation = _administrativeUnits.filter {
                administrativeUnit -> run {
                val addressName = address.name(administrativeUnit = administrativeUnit)
                !addressRetrievingLocationSet.contains(addressName)
            }
        }

        for (administrativeUnit in administrativeUnitsWithNonRetrievedLocation) {
            val addressName = address.name(administrativeUnit = administrativeUnit)
            addressRetrievingLocationSet.add(addressName)
            externalScope.launch {
                log(
                    method = "retrieveLocationForAddress",
                    msg = "Retrieve Location for $addressName"
                )
                locationDataSource.retrieveLocationFor(
                    address = address,
                    administrativeUnit = administrativeUnit
                )
            }
        }
    }

    private fun assignNewLocationGeoLocation(
        administrativeUnit: AdministrativeUnit,
        addressName: String,
        location: Location? = null,
        initialGeoLocation: GeoLocation
    ) {
        val locationGeoLocation = (administrativeUnitAddressNameLocationGeoLocation[
            administrativeUnit
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
                        "to administrativeUnitAddressNameLocationGeoLocation[" +
                        "$administrativeUnit" +
                       "][$addressName]"
            )
            administrativeUnitAddressNameLocationGeoLocation[
                administrativeUnit
            ]?.set(addressName, newLocationGeoLocation)
        }
    }

    private fun log(method: String, msg: String) {
        Log.d(tag = "HomeRepository.$method", msg = msg)
    }
}