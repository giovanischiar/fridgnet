package io.schiar.fridgnet.model.repository.location

import io.schiar.fridgnet.model.Address
import io.schiar.fridgnet.model.Location

interface LocationDataSource {
    suspend fun fetchLocationBy(address: Address): Location?
}