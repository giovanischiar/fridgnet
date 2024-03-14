package io.schiar.fridgnet.model.repository

import io.schiar.fridgnet.Log
import io.schiar.fridgnet.model.Address
import io.schiar.fridgnet.model.AddressCoordinates
import io.schiar.fridgnet.model.AddressLocationCoordinate
import io.schiar.fridgnet.model.AdministrativeUnit
import io.schiar.fridgnet.model.Image
import io.schiar.fridgnet.model.Location
import io.schiar.fridgnet.model.datasource.AddressDataSource
import io.schiar.fridgnet.model.datasource.ImageDataSource
import io.schiar.fridgnet.model.datasource.LocationDataSource
import io.schiar.fridgnet.model.datasource.local.CurrentAddressLocationCoordinateLocalDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import java.util.Collections.synchronizedMap as syncMapOf

class HomeRepository(
    private val addressDataSource: AddressDataSource,
    private val locationDataSource: LocationDataSource,
    private val imageDataSource: ImageDataSource,
    private val currentAddressLocationCoordinateLocalDataSource
        : CurrentAddressLocationCoordinateLocalDataSource
) {
    private var _currentAdministrativeUnit = AdministrativeUnit.CITY
    private val _currentAdministrativeUnitStateFlow = MutableStateFlow(_currentAdministrativeUnit)
    private val _administrativeUnits = AdministrativeUnit.entries.toList()
    private val _administrativeUnitsStateFlow = MutableStateFlow(_administrativeUnits)
    private var _currentAddressLocationCoordinates: List<AddressLocationCoordinate> = emptyList()

    val administrativeUnits: Flow<List<AdministrativeUnit>> = _administrativeUnitsStateFlow
    val currentAdministrativeUnit: Flow<AdministrativeUnit> = _currentAdministrativeUnitStateFlow

    private val administrativeUnitAddressLocationCoordinate = syncMapOf(
        mutableMapOf<AdministrativeUnit, MutableMap<Address, AddressLocationCoordinate>>()
    )

    val addressLocationCoordinate = merge(
        imageDataSource.retrieve().onEach { images ->
            Log.d("", "Receiving ${images.size} images from database")
            images.forEach { onEachImage(image = it)
            } },
        addressDataSource.retrieve().onEach { addressesCoordinates ->
            Log.d("", "Addresses from Addresses ${addressesCoordinates.map { it.address }}")
            Log.d("", "Receiving ${addressesCoordinates.size} addresses from database")
            addressesCoordinates.forEach { onEachAddressCoordinates(addressCoordinates = it) }
        },
        locationDataSource.retrieve().onEach { locations ->
            Log.d("", "Receiving ${locations.size} locations from database")
            Log.d("", "Addresses from Locations ${locations.map { it.address }}")
            locations.forEach(::onEachLocation)
        },
        _currentAdministrativeUnitStateFlow.onEach { _currentAdministrativeUnit = it }
    ).map {
        _currentAddressLocationCoordinates = administrativeUnitAddressLocationCoordinate[
            _currentAdministrativeUnit
        ]?.values?.toList() ?: emptyList()
        _currentAddressLocationCoordinates
    }

    private suspend fun onEachImage(image: Image) {
        addressDataSource.createFrom(coordinate = image.coordinate)
    }

    private suspend fun onEachAddressCoordinates(addressCoordinates: AddressCoordinates) {
        for (address in addressCoordinates.address.allAddresses()) {
            val addressAddressLocationCoordinate = administrativeUnitAddressLocationCoordinate[
                address.administrativeUnit
            ]
            val addressLocationImages = addressAddressLocationCoordinate?.get(address)

            if (addressLocationImages?.address != null) { continue }

            val newAddressLocationCoordinate = AddressLocationCoordinate(
                address = address,
                initialCoordinate = addressCoordinates.coordinates[0]
            )

            if (addressAddressLocationCoordinate == null) {
                administrativeUnitAddressLocationCoordinate[
                    address.administrativeUnit
                ] = mutableMapOf(address to newAddressLocationCoordinate)
                Log.d("onEachAddressCoordinates", "Create the first key pair [${address.name()} to (${address.name()}, null ${addressCoordinates.coordinates[0]})] and assign to ${address.administrativeUnit}")
                Log.d("onEachAddressCoordinates", "Create location from ${address.name()}")
                locationDataSource.createFrom(address = address)
                continue
            }

            if (addressLocationImages == null) {
                Log.d("onEachAddressCoordinates", "Create a (${address.name()}, null ${addressCoordinates.coordinates[0]}), assign to ${address.name()} and then assign to ${address.administrativeUnit}")
                administrativeUnitAddressLocationCoordinate[
                    address.administrativeUnit
                ]?.set(address, newAddressLocationCoordinate)
                Log.d("onEachAddressCoordinates", "Create location from ${address.name()}")
                locationDataSource.createFrom(address = address)
                continue
            }

            Log.d("onEachAddressCoordinates", "Address is null that means on onEachLocation was created an AddressLocationCoordinate with just a location. So let's just add ${address.name()} to this object")
            administrativeUnitAddressLocationCoordinate[
                address.administrativeUnit
            ]?.set(address, addressLocationImages.with(
                address = address,
                initialCoordinate = addressCoordinates.coordinates[0]
            ))
        }
    }

    private fun onEachLocation(location: Location) {
        val address = location.address
        val addressAddressLocationCoordinate = administrativeUnitAddressLocationCoordinate[
            address.administrativeUnit
        ]
        val addressLocationCoordinate = addressAddressLocationCoordinate?.get(address)

        if (addressLocationCoordinate?.location != null) { return }

        val newAddressLocationCoordinate = AddressLocationCoordinate(location = location)

        if (addressAddressLocationCoordinate == null) {
            administrativeUnitAddressLocationCoordinate[
                address.administrativeUnit
            ] = mutableMapOf(address to newAddressLocationCoordinate)
            Log.d("onEachLocation", "Create the first key pair (${address.name()} to (null, ${location.id}, null)) and assign to ${address.administrativeUnit}")
            return
        }

        if (addressLocationCoordinate == null) {
            Log.d("onEachLocation", "Create a (null, ${location.id}, null), assign to ${address.name()} and then assign to ${address.administrativeUnit}")
            administrativeUnitAddressLocationCoordinate[
                address.administrativeUnit
            ]?.set(address, newAddressLocationCoordinate)
            return
        }

        Log.d("onEachLocation", "(${address.name()}, null, ${addressLocationCoordinate.initialCoordinate}) exists so let's assign the location of id ${location.id} to it")
        administrativeUnitAddressLocationCoordinate[
            address.administrativeUnit
        ]?.set(address, addressLocationCoordinate.with(location = location))
    }

    fun selectAddressLocationCoordinateAt(index: Int) {
        currentAddressLocationCoordinateLocalDataSource.update(
            addressLocationCoordinate = _currentAddressLocationCoordinates[index]
        )
    }

    fun changeCurrentAdministrativeUnit(index: Int) {
        _currentAdministrativeUnitStateFlow.update { _administrativeUnits[index] }
    }

    suspend fun removeAllImages() {
        imageDataSource.delete()
    }
}