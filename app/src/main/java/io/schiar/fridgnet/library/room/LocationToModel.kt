package io.schiar.fridgnet.library.room

import io.schiar.fridgnet.library.room.entity.CoordinateEntity
import io.schiar.fridgnet.library.room.entity.LocationEntity
import io.schiar.fridgnet.model.Coordinate
import io.schiar.fridgnet.model.Location

fun List<Coordinate>.toCoordinateEntities(coordinatesID: Long): List<CoordinateEntity> {
    return map { it.toCoordinateEntity(coordinatesID = coordinatesID) }
}

fun List<Coordinate>.toCoordinateEntitiesWithID(coordinatesID: Long): List<CoordinateEntity> {
    return map { it.toCoordinateEntity(id = it.id, coordinatesID = coordinatesID) }
}

fun Location.toLocationEntity(): LocationEntity {
    return LocationEntity(
        addressLocationsID = address.id,
        administrativeUnit = administrativeUnit.toString(),
        boundingBoxSouthwest = boundingBox.southwest.toCoordinateEntity(),
        boundingBoxNortheast = boundingBox.northeast.toCoordinateEntity(),
        zIndex = zIndex
    )
}

fun Location.toLocationEntity(id: Long): LocationEntity {
    return LocationEntity(
        id = id,
        addressLocationsID = address.id,
        administrativeUnit = administrativeUnit.toString(),
        boundingBoxSouthwest = boundingBox.southwest.toCoordinateEntity(),
        boundingBoxNortheast = boundingBox.northeast.toCoordinateEntity(),
        zIndex = zIndex
    )
}