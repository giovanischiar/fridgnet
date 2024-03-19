package io.schiar.fridgnet.model.datasource.local

import io.schiar.fridgnet.Log
import io.schiar.fridgnet.model.Address
import io.schiar.fridgnet.model.AddressLocationsCoordinates
import io.schiar.fridgnet.model.AdministrativeUnit
import io.schiar.fridgnet.model.Coordinate
import io.schiar.fridgnet.model.datasource.AddressDataSource
import io.schiar.fridgnet.model.datasource.retriever.AddressRetriever
import io.schiar.fridgnet.model.service.AddressService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.onEach
import java.util.Collections.synchronizedSet as syncSetOf

class AddressCoordinatesDataSource(
    private val addressRetriever: AddressRetriever,
    private val addressService: AddressService
): AddressDataSource {
    private val coordinateSet = syncSetOf(mutableSetOf<Coordinate>())

    override suspend fun create(coordinate: Coordinate, address: Address) {
        log(coordinate, "creating address ${address.name()}")
        addressService.create(coordinate = coordinate, address = address)
    }

    private fun updateCacheFromService(addressesCoordinates: List<AddressLocationsCoordinates>) {
        addressesCoordinates.forEach { addressCoordinates ->
            coordinateSet.addAll(elements = addressCoordinates.coordinates)
        }
    }

    override suspend fun retrieveAddressFor(coordinate: Coordinate) {
        if (coordinateSet.contains(element = coordinate)) return
        coordinateSet.add(element = coordinate)
        log(coordinate = coordinate, "It's not on memory, retrieving using the Geocoder")
        val addressFromRetriever = addressRetriever.retrieve(coordinate = coordinate)
        if (addressFromRetriever != null) {
            create(coordinate = coordinate, address = addressFromRetriever)
            return
        }
        log(coordinate = coordinate, "It's not on the Geocoder!")
    }

    override fun retrieve(): Flow<List<AddressLocationsCoordinates>> {
        return addressService.retrieve().onEach(::updateCacheFromService)
    }

    override fun retrieveCoordinates(
        address: Address, administrativeUnit: AdministrativeUnit
    ): Flow<List<Coordinate>> {
        return addressService.retrieveCoordinates(
            address = address, administrativeUnit = administrativeUnit
        )
    }

    private fun log(coordinate: Coordinate, msg: String) {
        val (_, latitude, longitude) = coordinate
        Log.d(
            tag = "Coordinate to Address Feature",
            msg = "Retrieving Address for ($latitude, $longitude): $msg"
        )
    }
}