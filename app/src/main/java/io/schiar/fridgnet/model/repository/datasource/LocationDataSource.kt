package io.schiar.fridgnet.model.repository.datasource

import io.schiar.fridgnet.model.Address
import io.schiar.fridgnet.model.Location

interface LocationDataSource {
    suspend fun fetchCity(address: Address): Location?
    suspend fun fetchCounty(address: Address): Location?
    suspend fun fetchState(address: Address): Location?
    suspend fun fetchCountry(address: Address): Location?
}