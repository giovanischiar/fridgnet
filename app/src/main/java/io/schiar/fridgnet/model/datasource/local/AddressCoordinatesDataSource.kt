package io.schiar.fridgnet.model.datasource.local

import io.schiar.fridgnet.Log
import io.schiar.fridgnet.model.Address
import io.schiar.fridgnet.model.AddressCoordinates
import io.schiar.fridgnet.model.Coordinate
import io.schiar.fridgnet.model.datasource.AddressDataSource
import io.schiar.fridgnet.model.datasource.retriever.AddressRetriever
import io.schiar.fridgnet.model.service.AddressService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import java.util.Collections.synchronizedMap as syncMapOf
import java.util.Collections.synchronizedSet as syncSetOf

class AddressCoordinatesDataSource(
    private val addressRetriever: AddressRetriever,
    private val addressService: AddressService
): AddressDataSource {
    private val coordinateSet = syncSetOf(mutableSetOf<Coordinate>())
    private val addressAddressCoordinateCache = syncMapOf(
        mutableMapOf<Address, AddressCoordinates>()
    )
    private val addressesCoordinatesCacheFlow = MutableStateFlow(
        addressAddressCoordinateCache.values.toList()
    )

    override suspend fun create(coordinate: Coordinate, address: Address) {
        log(coordinate, "creating address ${address.name()}")
        addressAddressCoordinateCache[address] = addressAddressCoordinateCache[address]
            ?.with(coordinate = coordinate) ?: AddressCoordinates(
            address = address,
            coordinates = listOf(coordinate)
        )
        addressesCoordinatesCacheFlow.update { addressAddressCoordinateCache.values.toList() }
        addressService.create(coordinate = coordinate, address = address)
    }

    private fun updateCache(addressCoordinates: AddressCoordinates) {
        addressAddressCoordinateCache[addressCoordinates.address] = addressCoordinates
    }

    private fun updateCacheFromService(addressesCoordinates: List<AddressCoordinates>) {
        addressesCoordinates.forEach { addressCoordinates ->
            updateCache(addressCoordinates = addressCoordinates)
            coordinateSet.addAll(elements = addressCoordinates.coordinates)
        }
    }

    override suspend fun createFrom(coordinate: Coordinate) {
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

    override fun retrieve(): Flow<List<AddressCoordinates>> {
        return merge(
            addressesCoordinatesCacheFlow,
            addressService.retrieve().onEach(::updateCacheFromService)
        ).distinctUntilChanged()
    }

    override fun retrieveCoordinates(address: Address): Flow<List<Coordinate>> {
        return addressService.retrieveCoordinates(address = address)
    }

    private fun log(coordinate: Coordinate, msg: String) {
        val (_, latitude, longitude) = coordinate
        Log.d(
            tag = "Coordinate to Address Feature",
            msg = "Retrieving Address for ($latitude, $longitude): $msg"
        )
    }
}