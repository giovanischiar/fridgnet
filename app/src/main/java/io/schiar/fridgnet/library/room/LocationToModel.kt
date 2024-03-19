package io.schiar.fridgnet.library.room

import io.schiar.fridgnet.library.room.entity.GeoLocationEntity
import io.schiar.fridgnet.library.room.entity.LocationEntity
import io.schiar.fridgnet.model.GeoLocation
import io.schiar.fridgnet.model.Location

fun List<GeoLocation>.toGeoLocationEntities(geoLocationsID: Long): List<GeoLocationEntity> {
    return map { it.toGeoLocationEntity(geoLocationsID = geoLocationsID) }
}

fun List<GeoLocation>.toGeoLocationEntitiesWithID(geoLocationsID: Long): List<GeoLocationEntity> {
    return map { it.toGeoLocationEntity(id = it.id, geoLocationsID = geoLocationsID) }
}

fun Location.toLocationEntity(): LocationEntity {
    return LocationEntity(
        addressLocationsID = address.id,
        administrativeUnit = administrativeUnit.toString(),
        boundingBoxSouthwest = boundingBox.southwest.toGeoLocationEntity(),
        boundingBoxNortheast = boundingBox.northeast.toGeoLocationEntity(),
        zIndex = zIndex
    )
}

fun Location.toLocationEntity(id: Long): LocationEntity {
    return LocationEntity(
        id = id,
        addressLocationsID = address.id,
        administrativeUnit = administrativeUnit.toString(),
        boundingBoxSouthwest = boundingBox.southwest.toGeoLocationEntity(),
        boundingBoxNortheast = boundingBox.northeast.toGeoLocationEntity(),
        zIndex = zIndex
    )
}