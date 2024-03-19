package io.schiar.fridgnet.model.datasource

import io.schiar.fridgnet.model.CartographicBoundaryGeoLocation
import kotlinx.coroutines.flow.Flow

interface CurrentCartographicBoundaryGeoLocationDataSource {
    fun retrieve(): Flow<CartographicBoundaryGeoLocation?>
    fun update(cartographicBoundaryGeoLocation: CartographicBoundaryGeoLocation)
}