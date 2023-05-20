package io.schiar.fridgnet.model

import kotlin.math.abs

data class Coordinate(val latitude: Double, val longitude: Double) {
    constructor(latitude: Int, longitude: Int) :
            this(latitude = latitude.toDouble(), longitude = longitude.toDouble())

    fun wasAntimeridianCrossed(other: Double): Boolean {
        val (_, longitude) = this
        return (longitude > 0.0 && other < 0.0 || longitude < 0.0 && other > 0.0) &&
                abs(other - longitude) > 180.0
    }
}