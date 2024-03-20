package io.schiar.fridgnet.library.room

import io.schiar.fridgnet.library.room.entity.CartographicBoundaryEntity
import io.schiar.fridgnet.library.room.entity.GeoLocationEntity
import io.schiar.fridgnet.model.CartographicBoundary
import io.schiar.fridgnet.model.GeoLocation

fun List<GeoLocation>.toGeoLocationEntities(geoLocationsID: Long): List<GeoLocationEntity> {
    return map { it.toGeoLocationEntity(geoLocationsID = geoLocationsID) }
}

fun List<GeoLocation>.toGeoLocationEntitiesWithID(geoLocationsID: Long): List<GeoLocationEntity> {
    return map { it.toGeoLocationEntity(id = it.id, geoLocationsID = geoLocationsID) }
}

fun CartographicBoundary.toCartographicBoundaryEntity(): CartographicBoundaryEntity {
    return CartographicBoundaryEntity(
        administrativeUnitNameCartographicBoundariesID = administrativeUnitName.id,
        administrativeLevel = administrativeLevel.toString(),
        boundingBoxSouthwest = boundingBox.southwest.toGeoLocationEntity(),
        boundingBoxNortheast = boundingBox.northeast.toGeoLocationEntity(),
        zIndex = zIndex
    )
}

fun CartographicBoundary.toCartographicBoundaryEntity(id: Long): CartographicBoundaryEntity {
    return CartographicBoundaryEntity(
        id = id,
        administrativeUnitNameCartographicBoundariesID = administrativeUnitName.id,
        administrativeLevel = administrativeLevel.toString(),
        boundingBoxSouthwest = boundingBox.southwest.toGeoLocationEntity(),
        boundingBoxNortheast = boundingBox.northeast.toGeoLocationEntity(),
        zIndex = zIndex
    )
}