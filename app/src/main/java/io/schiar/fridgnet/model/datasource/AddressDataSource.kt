package io.schiar.fridgnet.model.datasource

import io.schiar.fridgnet.model.Address
import io.schiar.fridgnet.model.Coordinate
import io.schiar.fridgnet.model.datasource.retriever.AddressRetriever

interface AddressDataSource: AddressRetriever {
    suspend fun setup(onLoaded: suspend (coordinate: Coordinate, address: Address) -> Unit)
    fun create(coordinate: Coordinate, address: Address)
}