package io.schiar.fridgnet.model.service

import io.schiar.fridgnet.model.Address
import io.schiar.fridgnet.model.AddressCoordinates
import io.schiar.fridgnet.model.Coordinate
import kotlinx.coroutines.flow.Flow

interface AddressService {
    suspend fun create(coordinate: Coordinate, address: Address)
    fun retrieve(): Flow<List<AddressCoordinates>>
    fun retrieveCoordinates(address: Address): Flow<List<Coordinate>>
    suspend fun retrieve(coordinate: Coordinate): AddressCoordinates?
}