package io.schiar.fridgnet.library.room

import io.schiar.fridgnet.library.room.relationentity.ImageWithCoordinate
import io.schiar.fridgnet.library.room.relationentity.LocationWithRegions
import io.schiar.fridgnet.library.room.relationentity.PolygonWithCoordinates
import io.schiar.fridgnet.library.room.relationentity.RegionWithPolygonAndHoles
import io.schiar.fridgnet.model.Address
import io.schiar.fridgnet.model.AdministrativeUnit
import io.schiar.fridgnet.model.BoundingBox
import io.schiar.fridgnet.model.Image
import io.schiar.fridgnet.model.Location
import io.schiar.fridgnet.model.Polygon
import io.schiar.fridgnet.model.Region

fun ImageWithCoordinate.toImage(): Image {
    return Image(
        uri = imageEntity.uri,
        byteArray = imageEntity.byteArray,
        date = imageEntity.date,
        coordinate = coordinate.toCoordinate()
    )
}

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