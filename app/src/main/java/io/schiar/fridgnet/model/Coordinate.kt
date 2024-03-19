package io.schiar.fridgnet.model

import kotlin.math.abs

data class Coordinate(val id: Long = 0, val latitude: Double, val longitude: Double) {
    constructor(latitude: Int, longitude: Int) :
            this(latitude = latitude.toDouble(), longitude = longitude.toDouble())

    fun wasAntimeridianCrossed(other: Double): Boolean {
        val (_, _, longitude) = this
        return (longitude > 0.0 && other < 0.0 || longitude < 0.0 && other > 0.0) &&
                abs(other - longitude) > 180.0
    }

    fun toBoundingBox(): BoundingBox {
        return BoundingBox(southwest = this, northeast = this)
    }

    override fun equals(other: Any?): Boolean {
        if (other !is Coordinate) return false
        return latitude == other.latitude && longitude == other.longitude
    }

    override fun hashCode(): Int {
        var result = latitude.hashCode()
        result = 31 * result + longitude.hashCode()
        return result
    }

    override fun toString(): String {
        return "($latitude, $longitude)"
    }
}