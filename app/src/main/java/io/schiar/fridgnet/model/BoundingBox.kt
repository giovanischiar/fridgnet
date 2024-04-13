package io.schiar.fridgnet.model

import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

/**
 * The bounding box is the delimited area of something, it could be the map the user is seeing or
 * the square area of a region of a cartographic boundary.
 *
 * @property southwest the geo location of the most southern point.
 * @property northeast the geo location of the most northern point.
 */
data class BoundingBox(val southwest: GeoLocation, val northeast: GeoLocation) {

    /**
     * @return true if the there is the antimeridian (-180) inside the boundingbox, false otherwise
     */
    fun containsAntimeridian(): Boolean {
        return abs(northeast.longitude - southwest.longitude) > 180.0
    }

    private fun containsLatitude(latitude: Double): Boolean {
        return latitude >= southwest.latitude && latitude <= northeast.latitude
    }

    /**
     * @return true if the bounding box contains geoLocation, false otherwise.
     */
    fun contains(geoLocation: GeoLocation): Boolean {
        return containsLatitude(latitude = geoLocation.latitude) &&
                containsLongitude(longitude = geoLocation.longitude)
    }

    private fun southOfLatitude(latitude: Double): Boolean {
        return latitude <= southwest.latitude
    }

    private fun northOfLatitude(latitude: Double): Boolean {
        return latitude >= northeast.latitude
    }

    private fun containsLongitude(longitude: Double): Boolean {
        return if (!containsAntimeridian()) {
            longitude >= southwest.longitude && longitude <= northeast.longitude
        } else {
            longitude >= southwest.longitude && longitude <= 180.0 ||
                    longitude >= -180.0 && longitude <= northeast.longitude
        }
    }

    private fun centerLongitude(): Double {
        return if (!containsAntimeridian()) {
            (northeast.longitude + southwest.longitude) / 2.0
        } else {
            val distanceFromAntimeridianToNortheast = 180.0 + northeast.longitude
            val distanceFromSouthwestToAntimeridian = 180.0 - southwest.longitude
            val halfDistance =
                (distanceFromSouthwestToAntimeridian + distanceFromAntimeridianToNortheast) / 2
            if (southwest.longitude + halfDistance >= 180.0) {
                val restOfTheDistance = southwest.longitude + halfDistance - 180.0
                -180.0 + restOfTheDistance
            } else {
                southwest.longitude + halfDistance
            }
        }
    }

    /**
     * @return the center geo location of the bounding box.
     */
    fun center(): GeoLocation {
        return GeoLocation(
            latitude = (northeast.latitude + southwest.latitude) / 2.0,
            longitude = centerLongitude()
        )
    }

    private fun centerAntipode(): Double {
        val centerLongitude = centerLongitude()
        return centerLongitude + if (centerLongitude < 0) 180.0 else -180.0
    }

    private fun westOfLongitude(longitude: Double): Boolean {
        val centerAntipode = centerAntipode()

        return if (!containsAntimeridian()) {
            if (centerAntipode <= 0.0) {
                longitude in centerAntipode..southwest.longitude
            } else {
                longitude in -180.0..southwest.longitude ||
                        longitude in centerAntipode..180.0
            }
        } else {
            longitude in centerAntipode..southwest.longitude
        }
    }

    private fun eastOfLongitude(longitude: Double): Boolean {
        val centerAntipode = centerAntipode()

        return if (!containsAntimeridian()) {
            if (centerAntipode >= 0.0) {
                longitude in northeast.longitude..centerAntipode
            } else {
                longitude in northeast.longitude..180.0 ||
                        longitude in -180.0..centerAntipode
            }
        } else {
            longitude in northeast.longitude..centerAntipode
        }
    }

    /**
     * @return the bounding box that represents the sum of two bounding boxes.
     */
    operator fun plus(other: BoundingBox): BoundingBox {
        return BoundingBox(
            southwest = GeoLocation(
                latitude = min(southwest.latitude, other.southwest.latitude),
                longitude = min(southwest.longitude, other.southwest.longitude)
            ),

            northeast = GeoLocation(
                latitude = max(northeast.latitude, other.northeast.latitude),
                longitude = max(northeast.longitude, other.northeast.longitude)
            )
        )
    }

    /**
     * @return the bounding box that represents the sum of a bounding box and a geo location.
     */
    operator fun plus(other: GeoLocation): BoundingBox {
        return plus(other = BoundingBox(southwest = other, northeast = other))
    }

    /**
     * Verifies if there is any intersection between two bounding boxes. The method also considers
     * whether both of the bounding boxes cross the antimeridian.
     *
     * @param other the bounding box used.
     * @return      true if the bounding box intersects somehow with the other bounding box, false otherwise.
     */
    fun contains(other: BoundingBox): Boolean {
        val southwestOrNortheastInside = contains(other.northeast) || contains(other.southwest)

        val northwestCorner = containsLatitude(latitude = other.southwest.latitude) &&
                westOfLongitude(longitude = other.southwest.longitude) &&
                northOfLatitude(latitude = other.northeast.latitude) &&
                containsLongitude(longitude = other.northeast.longitude)

        val southeastCorner = southOfLatitude(latitude = other.southwest.latitude) &&
                containsLongitude(longitude = other.southwest.longitude) &&
                containsLatitude(latitude = other.northeast.latitude) &&
                eastOfLongitude(longitude = other.northeast.longitude)

        val otherNorthOfBounds = containsLatitude(latitude = other.southwest.latitude) &&
                westOfLongitude(longitude = other.southwest.longitude) &&
                northOfLatitude(latitude = other.northeast.latitude) &&
                eastOfLongitude(longitude = other.northeast.longitude)

        val otherEastOfBounds = southOfLatitude(latitude = other.southwest.latitude) &&
                containsLongitude(longitude = other.southwest.longitude) &&
                northOfLatitude(latitude = other.northeast.latitude) &&
                eastOfLongitude(longitude = other.northeast.longitude)

        val otherSouthOfBounds = southOfLatitude(latitude = other.southwest.latitude) &&
                westOfLongitude(longitude = other.southwest.longitude) &&
                containsLatitude(latitude = other.northeast.latitude) &&
                eastOfLongitude(longitude = other.northeast.longitude)

        val otherWestOfBounds = southOfLatitude(latitude = other.southwest.latitude) &&
                westOfLongitude(longitude = other.southwest.longitude) &&
                northOfLatitude(latitude = other.northeast.latitude) &&
                containsLongitude(longitude = other.northeast.longitude)

        val otherNorthSouthOfBounds = southOfLatitude(latitude = other.southwest.latitude) &&
                containsLongitude(longitude = other.southwest.longitude) &&
                northOfLatitude(latitude = other.northeast.latitude) &&
                containsLongitude(longitude = other.northeast.longitude)

        val otherEastWestOfBounds = containsLatitude(latitude = other.southwest.latitude) &&
                westOfLongitude(longitude = other.southwest.longitude) &&
                containsLatitude(latitude = other.northeast.latitude) &&
                eastOfLongitude(longitude = other.northeast.longitude)

        val otherWrapBounds = other.contains(geoLocation = southwest) &&
                other.contains(geoLocation = northeast)

        return southwestOrNortheastInside ||
                northwestCorner ||
                southeastCorner ||
                otherNorthOfBounds ||
                otherEastOfBounds ||
                otherSouthOfBounds ||
                otherWestOfBounds ||
                otherNorthSouthOfBounds ||
                otherEastWestOfBounds ||
                otherWrapBounds
    }
}

