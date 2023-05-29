package io.schiar.fridgnet.model.repository

import io.schiar.fridgnet.model.Address
import io.schiar.fridgnet.model.Location

interface LocationRepository {
    suspend fun fetch(address: Address, onLocationReady: (location: Location) -> Unit)
}