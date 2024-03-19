package io.schiar.fridgnet.model

data class LocationCoordinate(
    val location: Location? = null,
    val initialCoordinate: Coordinate? = null
) {
    fun with(location: Location): LocationCoordinate {
        return LocationCoordinate(
            location = location,
            initialCoordinate = initialCoordinate
        )
    }

    fun with(initialCoordinate: Coordinate): LocationCoordinate {
        return LocationCoordinate(
            location = location,
            initialCoordinate = initialCoordinate
        )
    }

    override fun toString(): String {
        return "LocationCoordinate($location, $initialCoordinate)"
    }
}