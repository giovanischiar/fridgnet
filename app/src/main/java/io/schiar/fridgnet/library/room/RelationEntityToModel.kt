package io.schiar.fridgnet.library.room

import io.schiar.fridgnet.library.room.relationentity.AddressWithLocationsAndCoordinates
import io.schiar.fridgnet.library.room.relationentity.ImageWithAddressAndCoordinate
import io.schiar.fridgnet.library.room.relationentity.LocationWithRegions
import io.schiar.fridgnet.library.room.relationentity.PolygonWithCoordinates
import io.schiar.fridgnet.library.room.relationentity.RegionWithPolygonAndHoles
import io.schiar.fridgnet.model.AddressLocationsCoordinates
import io.schiar.fridgnet.model.AdministrativeUnit
import io.schiar.fridgnet.model.BoundingBox
import io.schiar.fridgnet.model.Image
import io.schiar.fridgnet.model.ImageAddress
import io.schiar.fridgnet.model.Location
import io.schiar.fridgnet.model.Polygon
import io.schiar.fridgnet.model.Region


fun List<AddressWithLocationsAndCoordinates>.toAddressesCoordinates(): List<AddressLocationsCoordinates> {
    return map { it.toAddressCoordinates() }
}

fun List<ImageWithAddressAndCoordinate>.toImageAddresses(): List<ImageAddress>{
    return map { it.toImageAddress() }
}

fun List<ImageWithAddressAndCoordinate>.toImages(): List<Image>{
    return map { it.toImage() }
}

fun AddressWithLocationsAndCoordinates.toAddressCoordinates(): AddressLocationsCoordinates {
    return AddressLocationsCoordinates(
        address = addressEntity.toAddress(),
        administrativeUnitLocation = locationEntities.toAdministrativeUnitLocation(),
        coordinates = coordinateEntities.toCoordinates()
    )
}

fun ImageWithAddressAndCoordinate.toImage(): Image {
    return Image(
        uri = imageEntity.uri,
        byteArray = imageEntity.byteArray,
        date = imageEntity.date,
        coordinate = coordinateWithAddress.coordinateEntity.toCoordinate()
    )
}

fun ImageWithAddressAndCoordinate.toImageAddress(): ImageAddress {
    return ImageAddress(
        image = Image(
            uri = imageEntity.uri,
            byteArray = imageEntity.byteArray,
            date = imageEntity.date,
            coordinate = coordinateWithAddress.coordinateEntity.toCoordinate()
        ),
        address = coordinateWithAddress.addressEntity?.toAddress()
    )
}

fun List<LocationWithRegions>.toAdministrativeUnitLocation(): Map<AdministrativeUnit, Location> {
    return associate {
        AdministrativeUnit.valueOf(value = it.locationEntity.administrativeUnit) to it.toLocation()
    }
}


fun List<LocationWithRegions>.toLocations(): List<Location> {
    return map { it.toLocation() }
}

fun LocationWithRegions.toLocation(): Location {
    return Location(
        id = locationEntity.id,
        address = addressEntity.toAddress(),
        regions = regionEntities.map { it.toRegion() },
        boundingBox = BoundingBox(
            southwest = locationEntity.boundingBoxSouthwest.toCoordinate(),
            northeast = locationEntity.boundingBoxNortheast.toCoordinate(),
        ),
        zIndex = locationEntity.zIndex,
        administrativeUnit = AdministrativeUnit.valueOf(value = locationEntity.administrativeUnit)
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