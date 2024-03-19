package io.schiar.fridgnet.model

data class LocationGeoLocation(
    val location: Location? = null,
    val initialGeoLocation: GeoLocation? = null
) {
    fun with(location: Location): LocationGeoLocation {
        return LocationGeoLocation(
            location = location,
            initialGeoLocation = initialGeoLocation
        )
    }

    fun with(initialGeoLocation: GeoLocation): LocationGeoLocation {
        return LocationGeoLocation(
            location = location,
            initialGeoLocation = initialGeoLocation
        )
    }

    override fun toString(): String {
        return "LocationGeoLocation($location, $initialGeoLocation)"
    }
}