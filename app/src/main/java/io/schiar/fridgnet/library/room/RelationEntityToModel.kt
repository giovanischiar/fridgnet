package io.schiar.fridgnet.library.room

import io.schiar.fridgnet.library.room.relationentity.AddressWithCoordinates
import io.schiar.fridgnet.library.room.relationentity.ImageWithCoordinate
import io.schiar.fridgnet.library.room.relationentity.LocationWithRegions
import io.schiar.fridgnet.library.room.relationentity.PolygonWithCoordinates
import io.schiar.fridgnet.library.room.relationentity.RegionWithPolygonAndHoles
import io.schiar.fridgnet.model.Address
import io.schiar.fridgnet.model.AddressCoordinates
import io.schiar.fridgnet.model.AdministrativeUnit
import io.schiar.fridgnet.model.BoundingBox
import io.schiar.fridgnet.model.Image
import io.schiar.fridgnet.model.Location
import io.schiar.fridgnet.model.Polygon
import io.schiar.fridgnet.model.Region


fun List<AddressWithCoordinates>.toAddressesCoordinates(): List<AddressCoordinates> {
    return map { it.toAddressCoordinates() }
}

fun List<ImageWithCoordinate>.toImages(): List<Image>{
    return map { it.toImage() }
}

fun AddressWithCoordinates.toAddressCoordinates(): AddressCoordinates {
    return AddressCoordinates(
        address = addressEntity.toAddress(),
        coordinates = coordinates.toCoordinates()
    )
}

fun ImageWithCoordinate.toImage(): Image {
    return Image(
        uri = imageEntity.uri,
        byteArray = imageEntity.byteArray,
        date = imageEntity.date,
        coordinate = coordinate.toCoordinate()
    )
}

fun List<LocationWithRegions>.toLocations(): List<Location> {
    return map { it.toLocation() }
}

fun LocationWithRegions.toLocation(): Location {
    return Location(
        id = locationEntity.id,
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
    return Polygon(id = polygon.id, coordinates = coordinates.map { it.toCoordinate() })
}

fun List<RegionWithPolygonAndHoles>.toRegions(): List<Region> {
    return map { it.toRegion() }
}

fun RegionWithPolygonAndHoles.toRegion(): Region {
    return Region(
        id = regionEntity.id,
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