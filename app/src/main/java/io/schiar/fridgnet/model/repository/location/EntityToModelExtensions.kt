package io.schiar.fridgnet.model.repository.location

import io.schiar.fridgnet.model.Address
import io.schiar.fridgnet.model.AdministrativeUnit
import io.schiar.fridgnet.model.BoundingBox
import io.schiar.fridgnet.model.Coordinate
import io.schiar.fridgnet.model.Location
import io.schiar.fridgnet.model.Polygon
import io.schiar.fridgnet.model.Region
import io.schiar.fridgnet.model.datasource.room.entity.CoordinateEntity
import io.schiar.fridgnet.model.datasource.room.relationentity.LocationWithRegions
import io.schiar.fridgnet.model.datasource.room.relationentity.PolygonWithCoordinates
import io.schiar.fridgnet.model.datasource.room.relationentity.RegionWithPolygonAndHoles

fun LocationWithRegions.toLocation(): Location {
    return Location(
        address = Address(
            locality = locationEntity.locality,
            subAdminArea = locationEntity.subAdminArea,
            adminArea = locationEntity.adminArea,
            countryName = locationEntity.countryName,
            administrativeUnit = AdministrativeUnit.valueOf(value = locationEntity.administrativeUnit)
        ),
        regions = regions.map { it.toRegion() },
        boundingBox = BoundingBox(
            southwest = locationEntity.boundingBoxSouthwest.toCoordinate(),
            northeast = locationEntity.boundingBoxNortheast.toCoordinate(),
        ),
        zIndex = locationEntity.zIndex
    )
}

fun PolygonWithCoordinates.toPolygon(): Polygon {
    return Polygon(coordinates = coordinates.map { it.toCoordinate() })
}

fun RegionWithPolygonAndHoles.toRegion(): Region {
    return Region(
        polygon = polygon.toPolygon(),
        holes = holes.map { it.toPolygon() },
        active = regionEntity.active,
        boundingBox = BoundingBox(
            southwest = regionEntity.boundingBoxSouthwest.toCoordinate(),
            northeast = regionEntity.boundingBoxNortheast.toCoordinate(),
        ),
        zIndex = regionEntity.zIndex
    )
}

fun CoordinateEntity.toCoordinate(): Coordinate {
    return Coordinate(
        latitude = latitude,
        longitude = longitude
    )
}