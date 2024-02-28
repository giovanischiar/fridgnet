package io.schiar.fridgnet.library.room

import io.schiar.fridgnet.library.room.entity.CoordinateEntity
import io.schiar.fridgnet.library.room.entity.LocationEntity
import io.schiar.fridgnet.model.Coordinate
import io.schiar.fridgnet.model.Location

fun List<Coordinate>.toCoordinateEntities(coordinatesID: Long): List<CoordinateEntity> {
    return map { it.toCoordinateEntity(coordinatesID = coordinatesID) }
}

fun Location.toLocationEntity(): LocationEntity {
    return LocationEntity(
        locality = address.locality,
        subAdminArea = address.subAdminArea,
        adminArea = address.adminArea,
        countryName = address.countryName,
        administrativeUnit = address.administrativeUnit.toString(),
        boundingBoxSouthwest = boundingBox.southwest.toCoordinateEntity(),
        boundingBoxNortheast = boundingBox.northeast.toCoordinateEntity(),
        zIndex = zIndex
    )
}

fun Location.toLocationEntity(locationID: Long): LocationEntity {
    return LocationEntity(
        id = locationID,
        locality = address.locality,
        subAdminArea = address.subAdminArea,
        adminArea = address.adminArea,
        countryName = address.countryName,
        administrativeUnit = address.administrativeUnit.toString(),
        boundingBoxSouthwest = boundingBox.southwest.toCoordinateEntity(),
        boundingBoxNortheast = boundingBox.northeast.toCoordinateEntity(),
        zIndex = zIndex
    )
}