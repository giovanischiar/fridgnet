package io.schiar.fridgnet.model.datasource.retriever

import io.schiar.fridgnet.model.Address
import io.schiar.fridgnet.model.Location

interface LocationRetriever {
    suspend fun fetchLocationBy(address: Address): Location?
}