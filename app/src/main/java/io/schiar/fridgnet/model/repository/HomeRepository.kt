package io.schiar.fridgnet.model.repository

import io.schiar.fridgnet.Log
import io.schiar.fridgnet.model.AddressLocationsCoordinates
import io.schiar.fridgnet.model.AdministrativeUnit
import io.schiar.fridgnet.model.Image
import io.schiar.fridgnet.model.Location
import io.schiar.fridgnet.model.LocationCoordinate
import io.schiar.fridgnet.model.datasource.AddressDataSource
import io.schiar.fridgnet.model.datasource.ImageDataSource
import io.schiar.fridgnet.model.datasource.LocationDataSource
import io.schiar.fridgnet.model.datasource.local.CurrentAddressLocationCoordinateLocalDataSource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Collections.synchronizedMap as syncMapOf

class HomeRepository(
    private val addressDataSource: AddressDataSource,
    private val locationDataSource: LocationDataSource,
    private val imageDataSource: ImageDataSource,
    private val currentAddressLocationCoordinateLocalDataSource
        : CurrentAddressLocationCoordinateLocalDataSource,
    private val externalScope: CoroutineScope
) {
    private var _currentAdministrativeUnit = AdministrativeUnit.CITY
    private val _currentAdministrativeUnitStateFlow = MutableStateFlow(_currentAdministrativeUnit)
    private val _administrativeUnits = AdministrativeUnit.entries.toList()
    private val _administrativeUnitsStateFlow = MutableStateFlow(_administrativeUnits)
    private var _currentLocationCoordinates: List<LocationCoordinate> = emptyList()

    val administrativeUnits: Flow<List<AdministrativeUnit>> = _administrativeUnitsStateFlow
    val currentAdministrativeUnit: Flow<AdministrativeUnit> = _currentAdministrativeUnitStateFlow

    private val administrativeUnitLocationCoordinate = syncMapOf(
        mutableMapOf<AdministrativeUnit, MutableMap<String, LocationCoordinate>>()
    )

    val locationCoordinates = merge(
        imageDataSource.retrieve().onEach { images -> images.forEach(::onEachImage) },
        addressDataSource.retrieve().onEach { addressesLocationsCoordinatesList ->
            addressesLocationsCoordinatesList.forEach(::onEachAddressLocationsCoordinates)
        },
        locationDataSource.retrieve().onEach { locations -> locations.forEach(::onEachLocation) },
        _currentAdministrativeUnitStateFlow.onEach { _currentAdministrativeUnit = it }
    ).map {
        _currentLocationCoordinates = administrativeUnitLocationCoordinate[
            _currentAdministrativeUnit
        ]?.values?.toList() ?: emptyList()
        _currentLocationCoordinates
    }

    private fun onEachImage(image: Image) {
        externalScope.launch { addressDataSource.createFrom(coordinate = image.coordinate) }
    }

    private fun onEachAddressLocationsCoordinates(
        addressLocationsCoordinates: AddressLocationsCoordinates
    ) {
        val address = addressLocationsCoordinates.address
        val initialCoordinate = addressLocationsCoordinates.coordinates[0]
        val administrativeUnitLocation = addressLocationsCoordinates.administrativeUnitLocation
        for (administrativeUnit in AdministrativeUnit.entries.toList()) {
            val addressName = address.name(administrativeUnit = administrativeUnit)
            val addressAddressLocationCoordinate = administrativeUnitLocationCoordinate[
                administrativeUnit
            ]
            val locationCoordinate = addressAddressLocationCoordinate?.get(addressName)

            if (locationCoordinate?.initialCoordinate != null) { return }

            val newAddressLocationCoordinate = LocationCoordinate(
                location = administrativeUnitLocation[administrativeUnit],
                initialCoordinate = initialCoordinate
            )

            if (addressAddressLocationCoordinate == null) {
                administrativeUnitLocationCoordinate[
                    administrativeUnit
                ] = mutableMapOf(addressName to newAddressLocationCoordinate)
                Log.d("onEachAddressCoordinates", "Create the first key pair [$addressName to (${newAddressLocationCoordinate.location?.id}, ${initialCoordinate})] and assign to $administrativeUnit")
                if (newAddressLocationCoordinate.location == null) {
                    Log.d("onEachAddressCoordinates", "Create location from $addressName")
                    externalScope.launch {
                        locationDataSource.createFrom(
                            address = address,
                            administrativeUnit = administrativeUnit
                        )
                    }
                }
                continue
            }

            if (locationCoordinate == null) {
                Log.d("onEachAddressCoordinates", "Create a key pair [$addressName to (${newAddressLocationCoordinate.location?.id}, ${initialCoordinate})] and assign to $administrativeUnit")
                administrativeUnitLocationCoordinate[
                    administrativeUnit
                ]?.set(addressName, newAddressLocationCoordinate)
                if (newAddressLocationCoordinate.location == null) {
                    Log.d("onEachAddressCoordinates", "Create location from $addressName")
                    externalScope.launch {
                        locationDataSource.createFrom(
                            address = address,
                            administrativeUnit = administrativeUnit
                        )
                    }
                }
                continue
            }
        }
    }

    private fun onEachLocation(location: Location) {
        val addressName = location.addressName()
        val initialCoordinate = location.boundingBox.center()
        val administrativeUnit = location.administrativeUnit
        val addressAddressLocationCoordinate = administrativeUnitLocationCoordinate[
            administrativeUnit
        ]
        val addressLocationCoordinate = addressAddressLocationCoordinate?.get(addressName)

        if (addressLocationCoordinate?.location != null) { return }

        val newAddressLocationCoordinate = LocationCoordinate(
            location = location,
            initialCoordinate = initialCoordinate
        )

        if (addressAddressLocationCoordinate == null) {
            administrativeUnitLocationCoordinate[
                administrativeUnit
            ] = mutableMapOf(addressName to newAddressLocationCoordinate)
            Log.d("onEachLocation", "Create the first key pair ($addressName to (${location.id}, $initialCoordinate)) and assign to $administrativeUnit")
            return
        }

        Log.d("onEachLocation", "Create a (${location.id}, $initialCoordinate), assign to $addressName and then assign to $administrativeUnit")
        administrativeUnitLocationCoordinate[
            administrativeUnit
        ]?.set(addressName, newAddressLocationCoordinate)
    }

    fun selectAddressLocationCoordinateAt(index: Int) {
        val locationCoordinate = _currentLocationCoordinates[index]
        Log.d("", "LocationCoordinate at $index is $locationCoordinate")
        currentAddressLocationCoordinateLocalDataSource.update(
            addressLocationCoordinate = locationCoordinate
        )
    }

    fun changeCurrentAdministrativeUnit(index: Int) {
        _currentAdministrativeUnitStateFlow.update { _administrativeUnits[index] }
    }

    suspend fun removeAllImages() {
        imageDataSource.delete()
    }
}