package io.schiar.fridgnet.model.datasource.retriever

import io.schiar.fridgnet.model.Address
import io.schiar.fridgnet.model.Coordinate

interface AddressRetriever {
    suspend fun retrieve(coordinate: Coordinate): Address?
}