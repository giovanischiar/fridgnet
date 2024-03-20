package io.schiar.fridgnet.library.room

import io.schiar.fridgnet.library.room.entity.GeoLocationEntity
import io.schiar.fridgnet.model.GeoLocation

fun GeoLocation.toGeoLocationEntity(administrativeUnitNameGeoLocationsID: Long): GeoLocationEntity {
    return GeoLocationEntity(
        id = id,
        administrativeUnitNameGeoLocationsID = administrativeUnitNameGeoLocationsID,
        latitude = latitude,
        longitude = longitude
    )
}

fun GeoLocation.toGeoLocationEntity(geoLocationsID: Long? = null): GeoLocationEntity {
    return GeoLocationEntity(
        geoLocationsID = geoLocationsID,
        latitude = latitude,
        longitude = longitude
    )
}

fun GeoLocation.toGeoLocationEntity(id: Long, geoLocationsID: Long? = null): GeoLocationEntity {
    return GeoLocationEntity(
        id = id,
        geoLocationsID = geoLocationsID,
        latitude = latitude,
        longitude = longitude
    )
}