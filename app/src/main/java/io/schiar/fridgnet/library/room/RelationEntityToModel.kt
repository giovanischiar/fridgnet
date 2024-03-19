package io.schiar.fridgnet.library.room

import io.schiar.fridgnet.library.room.relationentity.AddressWithLocationsAndGeoLocations
import io.schiar.fridgnet.library.room.relationentity.ImageWithAddressAndGeoLocation
import io.schiar.fridgnet.library.room.relationentity.LocationWithRegions
import io.schiar.fridgnet.library.room.relationentity.PolygonWithGeoLocations
import io.schiar.fridgnet.library.room.relationentity.RegionWithPolygonAndHoles
import io.schiar.fridgnet.model.AddressLocationsGeoLocations
import io.schiar.fridgnet.model.AdministrativeLevel
import io.schiar.fridgnet.model.BoundingBox
import io.schiar.fridgnet.model.Image
import io.schiar.fridgnet.model.ImageAddress
import io.schiar.fridgnet.model.Location
import io.schiar.fridgnet.model.Polygon
import io.schiar.fridgnet.model.Region


fun List<AddressWithLocationsAndGeoLocations>.toAddressLocationsGeoLocations(): List<AddressLocationsGeoLocations> {
    return map { it.toAddressLocationsGeoLocations() }
}

fun List<ImageWithAddressAndGeoLocation>.toImageAddresses(): List<ImageAddress>{
    return map { it.toImageAddress() }
}

fun List<ImageWithAddressAndGeoLocation>.toImages(): List<Image>{
    return map { it.toImage() }
}

fun AddressWithLocationsAndGeoLocations.toAddressLocationsGeoLocations(): AddressLocationsGeoLocations {
    return AddressLocationsGeoLocations(
        address = addressEntity.toAddress(),
        administrativeLevelLocation = locationEntities.toAdministrativeLevelLocation(),
        geoLocations = geoLocationEntities.toGeoLocations()
    )
}

fun ImageWithAddressAndGeoLocation.toImage(): Image {
    return Image(
        uri = imageEntity.uri,
        byteArray = imageEntity.byteArray,
        date = imageEntity.date,
        geoLocation = geoLocationWithAddress.geoLocationEntity.toGeoLocation()
    )
}

fun ImageWithAddressAndGeoLocation.toImageAddress(): ImageAddress {
    return ImageAddress(
        image = Image(
            uri = imageEntity.uri,
            byteArray = imageEntity.byteArray,
            date = imageEntity.date,
            geoLocation = geoLocationWithAddress.geoLocationEntity.toGeoLocation()
        ),
        address = geoLocationWithAddress.addressEntity?.toAddress()
    )
}

fun List<LocationWithRegions>.toAdministrativeLevelLocation(): Map<AdministrativeLevel, Location> {
    return associate {
        AdministrativeLevel.valueOf(value = it.locationEntity.administrativeLevel) to it.toLocation()
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
            southwest = locationEntity.boundingBoxSouthwest.toGeoLocation(),
            northeast = locationEntity.boundingBoxNortheast.toGeoLocation(),
        ),
        zIndex = locationEntity.zIndex,
        administrativeLevel = AdministrativeLevel.valueOf(value = locationEntity.administrativeLevel)
    )
}

fun PolygonWithGeoLocations.toPolygon(): Polygon {
    return Polygon(id = polygon.id, geoLocations = getLocationEntitites.map { it.toGeoLocation() })
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
            southwest = regionEntity.boundingBoxSouthwest.toGeoLocation(),
            northeast = regionEntity.boundingBoxNortheast.toGeoLocation(),
        ),
        zIndex = regionEntity.zIndex
    )
}