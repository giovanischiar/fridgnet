package io.schiar.fridgnet.model

fun Address.name(): String {
    return if (this.locality != null) {
        "${this.locality}, ${this.subAdminArea}, ${this.adminArea}, ${this.countryName}"
    } else if (this.subAdminArea != null) {
        "${this.subAdminArea}, ${this.adminArea}, ${this.countryName}"
    } else if (this.adminArea != null) {
        "${this.adminArea}, ${this.countryName}"
    } else this.countryName ?: "null"
}

fun Polygon.findBoundingBox(): BoundingBox {
    var maxLatitude = Double.MIN_VALUE
    var maxLongitude = Double.MIN_VALUE
    var minLatitude = Double.MAX_VALUE
    var minLongitude = Double.MAX_VALUE

    this.coordinates.forEach {
        if (it.latitude > maxLatitude) { maxLatitude = it.latitude }

        if (it.longitude > maxLongitude) { maxLongitude = it.longitude }

        if (it.latitude < minLatitude) { minLatitude = it.longitude }

        if (it.longitude < minLongitude) { minLongitude = it.longitude }
    }

    return BoundingBox(
        northeast = Coordinate(latitude = maxLatitude, longitude = maxLongitude),
        southwest = Coordinate(latitude = minLatitude, longitude = minLongitude)
    )
}