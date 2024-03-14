package io.schiar.fridgnet.model.datasource

import io.schiar.fridgnet.model.Address
import io.schiar.fridgnet.model.AddressCoordinates
import io.schiar.fridgnet.model.Coordinate
import kotlinx.coroutines.flow.Flow

interface AddressDataSource {
    suspend fun create(coordinate: Coordinate, address: Address)
    suspend fun createFrom(coordinate: Coordinate)
    fun retrieve(): Flow<List<AddressCoordinates>>
    fun retrieveCoordinates(address: Address): Flow<List<Coordinate>>
}