package io.schiar.fridgnet.model.repository.datasource.util

import io.schiar.fridgnet.model.Coordinate
import io.schiar.fridgnet.model.Location
import io.schiar.fridgnet.model.Region
import io.schiar.fridgnet.model.repository.datasource.room.entity.CoordinateEntity
import io.schiar.fridgnet.model.repository.datasource.room.entity.LocationEntity
import io.schiar.fridgnet.model.repository.datasource.room.entity.RegionEntity

fun List<Coordinate>.toCoordinateEntities(coordinatesID: Long): List<CoordinateEntity> {
    return map { it.toCoordinateEntity(coordinatesID = coordinatesID) }
}

fun Coordinate.toCoordinateEntity(coordinatesID: Long? = null): CoordinateEntity {
    return CoordinateEntity(
        coordinatesID = coordinatesID,
        latitude = latitude,
        longitude = longitude
    )
}

fun Location.toLocationEntity(): LocationEntity {
    return LocationEntity(
        locality = address.locality,
        subAdminArea = address.subAdminArea,
        adminArea = address.adminArea,
        countryName = address.countryName,
        administrativeUnit = administrativeUnit.toString(),
        boundingBoxSouthwest = boundingBox.southwest.toCoordinateEntity(),
        boundingBoxNortheast = boundingBox.northeast.toCoordinateEntity(),
        zIndex = zIndex
    )
}

fun Region.toRegionEntity(regionsID: Long, polygonID: Long): RegionEntity {
    return RegionEntity(
        regionsID = regionsID,
        polygonID = polygonID,
        active = active,
        boundingBoxSouthwest = boundingBox.southwest.toCoordinateEntity(),
        boundingBoxNortheast = boundingBox.northeast.toCoordinateEntity(),
        zIndex = zIndex,
    )
}