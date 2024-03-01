package io.schiar.fridgnet.model.datasource

import io.schiar.fridgnet.model.Address
import io.schiar.fridgnet.model.AddressCoordinates
import io.schiar.fridgnet.model.Coordinate
import io.schiar.fridgnet.model.datasource.retriever.AddressRetriever
import kotlinx.coroutines.flow.Flow

interface AddressDataSource: AddressRetriever {
    fun retrieve(): Flow<List<AddressCoordinates>>
    suspend fun create(coordinate: Coordinate, address: Address)
}