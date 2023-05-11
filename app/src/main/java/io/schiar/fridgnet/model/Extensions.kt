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
    return (longitude > 0 && other < 0 || longitude < 0 && other > 0) &&
            abs(other - longitude) > 180
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