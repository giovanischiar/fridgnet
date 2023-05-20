package io.schiar.fridgnet.model

data class Polygon(val coordinates: List<Coordinate>) {
    fun findBoundingBox(): BoundingBox {
        var maxLatitude = Double.NEGATIVE_INFINITY
        var maxLongitude = Double.NEGATIVE_INFINITY
        var minLatitude = Double.POSITIVE_INFINITY
        var minLongitude = Double.POSITIVE_INFINITY

        var furtherEastAwayFromAntimeridianLongitude = -180.0
        var furtherWestAwayFromAntimeridianLongitude = 180.0

        var wasAntimeridianEverCrossed = false

        for (i in this.coordinates.indices) {
            val coordinate = this.coordinates[i]
            val (latitude, longitude) = coordinate

            if (latitude > maxLatitude) { maxLatitude = latitude }
            if (longitude > maxLongitude) { maxLongitude = longitude }
            if (latitude < minLatitude) { minLatitude = latitude }
            if (longitude < minLongitude) { minLongitude = longitude }

            if (wasAntimeridianEverCrossed) {
                if (longitude < 0 && longitude > furtherEastAwayFromAntimeridianLongitude) {
                    furtherEastAwayFromAntimeridianLongitude = longitude
                }

                if (longitude > 0 && longitude < furtherWestAwayFromAntimeridianLongitude) {
                    furtherWestAwayFromAntimeridianLongitude = longitude
                }
            }

            if (i+1 < this.coordinates.size) {
                val next = this.coordinates[i+1]
                wasAntimeridianEverCrossed = wasAntimeridianEverCrossed ||
                        coordinate.wasAntimeridianCrossed(next.longitude)
            }
        }

        val southwestLatitude = minLatitude
        val southwestLongitude = if (!wasAntimeridianEverCrossed) {
            minLongitude
        } else {
            furtherWestAwayFromAntimeridianLongitude
        }
        val northeastLatitude = maxLatitude
        val northeastLongitude = if (!wasAntimeridianEverCrossed) {
            maxLongitude
        } else {
            furtherEastAwayFromAntimeridianLongitude
        }

        return BoundingBox(
            southwest = Coordinate(latitude = southwestLatitude, longitude = southwestLongitude),
            northeast = Coordinate(latitude = northeastLatitude, longitude = northeastLongitude)
        )
    }
}

