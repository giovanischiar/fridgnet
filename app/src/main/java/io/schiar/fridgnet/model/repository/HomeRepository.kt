package io.schiar.fridgnet.model.repository

import io.schiar.fridgnet.Log
import io.schiar.fridgnet.model.Address
import io.schiar.fridgnet.model.AddressLocationsCoordinates
import io.schiar.fridgnet.model.AdministrativeUnit
import io.schiar.fridgnet.model.AdministrativeUnit.CITY
import io.schiar.fridgnet.model.Coordinate
import io.schiar.fridgnet.model.ImageAddress
import io.schiar.fridgnet.model.Location
import io.schiar.fridgnet.model.LocationCoordinate
import io.schiar.fridgnet.model.datasource.AddressDataSource
import io.schiar.fridgnet.model.datasource.ImageDataSource
import io.schiar.fridgnet.model.datasource.LocationDataSource
import io.schiar.fridgnet.model.datasource.local.CurrentLocationCoordinateDataSource
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
    private val currentLocationCoordinateDataSource : CurrentLocationCoordinateDataSource,
    private val externalScope: CoroutineScope
) {
    private var _currentAdministrativeUnit = CITY
    private val _administrativeUnits = AdministrativeUnit.entries
    private val _currentAdministrativeUnitStateFlow = MutableStateFlow(_currentAdministrativeUnit)
    private var _currentLocationCoordinates: List<LocationCoordinate> = emptyList()

    val administrativeUnits: Flow<List<AdministrativeUnit>> = MutableStateFlow(_administrativeUnits)
    val currentAdministrativeUnit: Flow<AdministrativeUnit> = _currentAdministrativeUnitStateFlow

    private val coordinateRetrievingAddressSet = syncSetOf(mutableSetOf<Coordinate>())
    private val addressRetrievingLocationSet = syncSetOf(mutableSetOf<String>())

    private val administrativeUnitAddressNameLocationCoordinate = run {
        syncMapOf(_administrativeUnits.associateWith { mutableMapOf<String, LocationCoordinate>() })
    }

    val locationCoordinates = merge(
        imageDataSource.retrieveWithAddress().onEach { it.forEach(::onEachImageAddress) },
        addressDataSource.retrieve().onEach { it.forEach(::onEachAddressLocationsCoordinates) },
        locationDataSource.retrieve().onEach { it.forEach(::onEachLocation) },
        _currentAdministrativeUnitStateFlow.onEach { _currentAdministrativeUnit = it }
    ).map {
        _currentLocationCoordinates = administrativeUnitAddressNameLocationCoordinate[
            _currentAdministrativeUnit
        ]?.values?.toList() ?: emptyList()
        _currentLocationCoordinates
    }

    private fun onEachImageAddress(imageAddress: ImageAddress) {
        val (image, addressRetrieved) = imageAddress
        val coordinate = image.coordinate
        if (addressRetrieved != null) {
            assignNewLocationCoordinate(
                administrativeUnit = CITY,
                addressName = addressRetrieved.name(),
                initialCoordinate = coordinate
            )
        }
        retrieveAddressForCoordinate(
            coordinate = coordinate,
            addressFromCoordinate = addressRetrieved
        )
    }

    private fun onEachAddressLocationsCoordinates(
        addressLocationsCoordinates: AddressLocationsCoordinates
    ) {
        val (address, coordinates, administrativeUnitLocation) = addressLocationsCoordinates
        val locationsRetrieved = administrativeUnitLocation.values
        assignNewLocationCoordinate(
            administrativeUnit = CITY,
            addressName = address.name(),
            location = administrativeUnitLocation[CITY],
            initialCoordinate = coordinates[0]
        )
        retrieveLocationsForAddress(address = address, locationsFromAddress = locationsRetrieved)
    }

    private fun onEachLocation(location: Location) {
        assignNewLocationCoordinate(
            administrativeUnit = location.administrativeUnit,
            addressName = location.addressName(),
            location = location,
            initialCoordinate = location.boundingBox.center()
        )
    }

    fun selectLocationCoordinateAt(index: Int) {
        val locationCoordinate = _currentLocationCoordinates[index]
        log(
            method = "selectLocationCoordinateAt",
            msg = "LocationCoordinate at $index is $locationCoordinate"
        )
        currentLocationCoordinateDataSource.update(locationCoordinate = locationCoordinate)
    }

    fun changeCurrentAdministrativeUnit(index: Int) {
        _currentAdministrativeUnitStateFlow.update { _administrativeUnits[index] }
    }

    suspend fun removeAllImages() { imageDataSource.delete() }

    private fun retrieveAddressForCoordinate(
        coordinate: Coordinate, addressFromCoordinate: Address?
    ) {
        if (addressFromCoordinate != null) coordinateRetrievingAddressSet.add(coordinate)
        val addressNotBeingRetrieved = !coordinateRetrievingAddressSet.contains(coordinate)
        if (addressNotBeingRetrieved) {
            coordinateRetrievingAddressSet.add(element = coordinate)
            log(method = "retrieveAddressForCoordinate", msg = "Retrieve Address for $coordinate")
            externalScope.launch { addressDataSource.retrieveAddressFor(coordinate = coordinate) }
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

    private fun assignNewLocationCoordinate(
        administrativeUnit: AdministrativeUnit,
        addressName: String,
        location: Location? = null,
        initialCoordinate: Coordinate
    ) {
        val locationCoordinate = (administrativeUnitAddressNameLocationCoordinate[
            administrativeUnit
        ] ?: return)[addressName]
        val needToAssignNewLocationCoordinate = locationCoordinate == null ||
                (locationCoordinate.location == null && location != null)

        if (needToAssignNewLocationCoordinate) {
            val newLocationCoordinate = LocationCoordinate(
                location = location,
                initialCoordinate = initialCoordinate
            )
            log(
                method = "assignLocationCoordinates",
                msg = "Assign $newLocationCoordinate " +
                        "to administrativeUnitAddressNameLocationCoordinate[" +
                        "$administrativeUnit" +
                       "][$addressName]"
            )
            administrativeUnitAddressNameLocationCoordinate[
                administrativeUnit
            ]?.set(addressName, newLocationCoordinate)
        }
    }

    private fun log(method: String, msg: String) {
        Log.d(tag = "HomeRepository.$method", msg = msg)
    }
}