package io.schiar.fridgnet.library.room

import io.schiar.fridgnet.library.room.entity.GeoLocationEntity
import io.schiar.fridgnet.model.GeoLocation

fun List<GeoLocationEntity>.toGeoLocations(): List<GeoLocation> {
    return map { it.toGeoLocation() }
}

fun GeoLocationEntity.toGeoLocation(): GeoLocation {
    return GeoLocation(
        id = id,
        latitude = latitude,
        longitude = longitude
    )
}