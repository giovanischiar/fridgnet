package io.schiar.fridgnet.library.room

import io.schiar.fridgnet.library.room.entity.GeoLocationEntity
import io.schiar.fridgnet.model.GeoLocation

/**
 * Converts the [GeoLocationEntity] into the [GeoLocation]
 *
 * @return the [GeoLocation] converted
 */
fun GeoLocationEntity.toGeoLocation(): GeoLocation {
    return GeoLocation(id = id, latitude = latitude, longitude = longitude)
}