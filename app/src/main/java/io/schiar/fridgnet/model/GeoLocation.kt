package io.schiar.fridgnet.model

import kotlin.math.abs

/**
 * Represents the geographic location of a point on a map using a spherical coordinate system.
 *
 * @property id        its database id.
 * @property latitude  the y-axis coordinate of the location in degrees, ranging from -90
 * (South Pole) to 90 (North Pole).
 * @property longitude the x-axis coordinate of the location in degrees, ranging from -180 (West) to
 * 180 (East).
 */
data class GeoLocation(val id: Long = 0, val latitude: Double, val longitude: Double) {
    constructor(latitude: Int, longitude: Int) :
            this(latitude = latitude.toDouble(), longitude = longitude.toDouble())

    /**
     * Checks if the antimeridian (longitude 180°) was crossed between this longitude and the
     * provided one.
     *
     * @param otherLongitude the other longitude value to compare with.
     * @return true if the antimeridian was crossed, false otherwise. This function uses
     * destructuring assignment to extract the longitude from the current object.
     */
    fun wasAntimeridianCrossed(otherLongitude: Double): Boolean {
        val (_, _, longitude) = this
        return (longitude > 0.0 && otherLongitude < 0.0 || longitude < 0.0 && otherLongitude > 0.0)
                && abs(otherLongitude - longitude) > 180.0
    }

    /**
     * Creates a degenerate bounding box (with zero area) by setting both southwest and northeast
     * corners to this [GeoLocation]. A single [GeoLocation] doesn't define an area in the
     * traditional bounding box sense.
     *
     * @return a new BoundingBox object with this GeoLocation as both southwest and northeast
     *         corner.
     */
    fun toBoundingBox(): BoundingBox {
        return BoundingBox(southwest = this, northeast = this)
    }

    /**
     * Checks if two GeoLocation objects are equal.
     * This method considers two GeoLocations equal if their latitude and longitude coordinates are
     * identical, excluding the id field (which might be used for database purposes).
     *
     * @param other The other [GeoLocation] object to compare with.
     * @return true if the [GeoLocation]s have the same latitude and longitude, false otherwise.
     */
    override fun equals(other: Any?): Boolean {
        if (other !is GeoLocation) return false
        return latitude == other.latitude && longitude == other.longitude
    }

    /**
     * Calculates a hash code for this GeoLocation object.
     * This method excludes the id field from the hash code calculation.
     * The hash code is based on the latitude and longitude values to ensure objects with the same
     * location have the same hash code. This is important for efficient use in hash-based
     * collections.
     *
     * @return an integer hash code value.
     */
    override fun hashCode(): Int {
        var result = latitude.hashCode()
        result = 31 * result + longitude.hashCode()
        return result
    }

    /**
     * Returns a string representation of the GeoLocation object in degrees for latitude and
     * longitude. This is useful for debug, test, and log purposes.
     *
     * @return a string representation of the GeoLocation in the format "(latitude, longitude)".
     */
    override fun toString(): String {
        return "($latitude, $longitude)"
    }
}