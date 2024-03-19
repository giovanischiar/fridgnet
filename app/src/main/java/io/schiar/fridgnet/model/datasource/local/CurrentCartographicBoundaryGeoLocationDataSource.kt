package io.schiar.fridgnet.model.datasource.local

import io.schiar.fridgnet.model.CartographicBoundaryGeoLocation
import io.schiar.fridgnet.model.datasource.CurrentCartographicBoundaryGeoLocationDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

class CurrentCartographicBoundaryGeoLocationDataSource:
    CurrentCartographicBoundaryGeoLocationDataSource {
    private val cartographicBoundaryGeoLocation = MutableStateFlow<CartographicBoundaryGeoLocation?>(
        value = null
    )

    override fun retrieve(): Flow<CartographicBoundaryGeoLocation?> {
        return cartographicBoundaryGeoLocation
    }

    override fun update(cartographicBoundaryGeoLocation: CartographicBoundaryGeoLocation) {
        this.cartographicBoundaryGeoLocation.update { cartographicBoundaryGeoLocation }
    }
}