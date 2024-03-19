package io.schiar.fridgnet.model

data class CartographicBoundaryGeoLocation(
    val cartographicBoundary: CartographicBoundary? = null,
    val initialGeoLocation: GeoLocation? = null
) {
    fun with(cartographicBoundary: CartographicBoundary): CartographicBoundaryGeoLocation {
        return CartographicBoundaryGeoLocation(
            cartographicBoundary = cartographicBoundary,
            initialGeoLocation = initialGeoLocation
        )
    }

    fun with(initialGeoLocation: GeoLocation): CartographicBoundaryGeoLocation {
        return CartographicBoundaryGeoLocation(
            cartographicBoundary = cartographicBoundary,
            initialGeoLocation = initialGeoLocation
        )
    }

    override fun toString(): String {
        return "CartographicBoundaryGeoLocation($cartographicBoundary, $initialGeoLocation)"
    }
}