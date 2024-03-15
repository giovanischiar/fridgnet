package io.schiar.fridgnet.model.datasource.retriever

import io.schiar.fridgnet.model.Address
import io.schiar.fridgnet.model.Location

interface LocationRetriever {
    suspend fun retrieveLocality(address: Address): Location?
    suspend fun retrieveSubAdmin(address: Address): Location?
    suspend fun retrieveAdmin(address: Address): Location?
    suspend fun retrieveCountry(address: Address): Location?
}