package io.schiar.fridgnet.model

import kotlin.math.abs

fun Address.name(): String {
    return if (this.locality != null) {
        "${this.locality}, ${this.subAdminArea}, ${this.adminArea}, ${this.countryName}"
    } else if (this.subAdminArea != null) {
        "${this.subAdminArea}, ${this.adminArea}, ${this.countryName}"
    } else if (this.adminArea != null) {
        "${this.adminArea}, ${this.countryName}"
    } else this.countryName ?: "null"
}

fun Coordinate.wasAntimeridianCrossed(other: Double): Boolean {
    val (_, longitude) = this
    return (longitude > 0.0 && other < 0.0 || longitude < 0.0 && other > 0.0) &&
            abs(other - longitude) > 180.0
}

fun Polygon.findBoundingBox(): BoundingBox {
    var maxLatitude = Double.NEGATIVE_INFINITY
    var maxLongitude = Double.NEGATIVE_INFINITY
    var minLatitude = Double.POSITIVE_INFINITY
    var minLongitude = Double.POSITIVE_INFINITY

    var wasAntimeridianEverCrossed = false

    for (i in this.coordinates.indices) {
        val coordinate = this.coordinates[i]
        val (latitude, longitude) = coordinate

        if (latitude > maxLatitude) { maxLatitude = latitude }

        if (longitude > maxLongitude) { maxLongitude = longitude }

        if (latitude < minLatitude) { minLatitude = latitude }

        if (longitude < minLongitude) { minLongitude = longitude }

        if (i+1 < this.coordinates.size) {
            val next = this.coordinates[i+1]
            wasAntimeridianEverCrossed = wasAntimeridianEverCrossed ||
                    coordinate.wasAntimeridianCrossed(next.longitude)
        }
    }

    val southwestLatitude = minLatitude
    val southwestLongitude = if (wasAntimeridianEverCrossed) maxLongitude else minLongitude
    val northeastLatitude = maxLatitude
    val northeastLongitude = if (wasAntimeridianEverCrossed) minLongitude else maxLongitude

    return BoundingBox(
        southwest = Coordinate(latitude = southwestLatitude, longitude = southwestLongitude),
        northeast = Coordinate(latitude = northeastLatitude, longitude = northeastLongitude)
    )
}

fun BoundingBox.containsAntimeridian(): Boolean {
    return abs(northeast.longitude - southwest.longitude) > 180.0
}

fun BoundingBox.containsLatitude(latitude: Double): Boolean {
    return latitude >= southwest.latitude && latitude <= northeast.latitude
}

fun BoundingBox.contains(coordinate: Coordinate): Boolean {
    return containsLatitude(latitude = coordinate.latitude) &&
            containsLongitude(longitude = coordinate.longitude)
}

fun BoundingBox.southOfLatitude(latitude: Double): Boolean {
    return latitude <= southwest.latitude
}

fun BoundingBox.northOfLatitude(latitude: Double): Boolean {
    return latitude >= northeast.latitude
}

fun BoundingBox.containsLongitude(longitude: Double): Boolean {
    return if (!containsAntimeridian()) {
        longitude >= southwest.longitude && longitude <= northeast.longitude
    } else {
        longitude >= southwest.longitude && longitude <= 180.0 ||
        longitude >= -180.0 && longitude <= northeast.longitude
    }
}

fun BoundingBox.centerLongitude(): Double {
    return if (!containsAntimeridian()) {
        (northeast.longitude + southwest.longitude) / 2.0
    } else {
        val distanceFromAntimeridianToNortheast = 180.0 + northeast.longitude
        val distanceFromSouthwestToAntimeridian = 180.0 - southwest.longitude
        val halfDistance = (distanceFromSouthwestToAntimeridian + distanceFromAntimeridianToNortheast)/2
        if (southwest.longitude + halfDistance >= 180.0) {
            val restOfTheDistance = southwest.longitude + halfDistance - 180.0
            -180.0 + restOfTheDistance
        } else {
            southwest.longitude + halfDistance
        }
    }
}

fun BoundingBox.centerAntipode(): Double {
    val centerLongitude = centerLongitude()
    return centerLongitude + if (centerLongitude < 0) 180.0 else - 180.0
}
fun BoundingBox.westOfLongitude(longitude: Double): Boolean {
    val centerAntipode = centerAntipode()

    return if (containsAntimeridian()) {
        longitude in centerAntipode..southwest.longitude
    } else {
         if (centerAntipode <= 0.0) {
             longitude in centerAntipode..southwest.longitude
        } else {
            longitude in -180.0..southwest.longitude || longitude in centerAntipode..180.0
        }
    }
}

fun BoundingBox.eastOfLongitude(longitude: Double): Boolean {
    val centerAntipode = centerAntipode()

    return if (containsAntimeridian()) {
        longitude in northeast.longitude..centerAntipode
    } else {
         if (centerAntipode >= 0.0) {
            longitude in northeast.longitude..centerAntipode
        } else {
            longitude in northeast.longitude..180.0 || longitude in -180.0..centerAntipode
        }
    }
}

fun BoundingBox.contains(other: BoundingBox): Boolean {
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

    val otherWrapBounds = other.contains(coordinate = southwest) &&
                          other.contains(coordinate = northeast)

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