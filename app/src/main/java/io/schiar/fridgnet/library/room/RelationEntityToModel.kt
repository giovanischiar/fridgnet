package io.schiar.fridgnet.library.room

import io.schiar.fridgnet.library.room.relationentity.AdministrativeUnitWithLocationsAndGeoLocations
import io.schiar.fridgnet.library.room.relationentity.ImageWithAdministrativeUnitAndGeoLocation
import io.schiar.fridgnet.library.room.relationentity.LocationWithRegions
import io.schiar.fridgnet.library.room.relationentity.PolygonWithGeoLocations
import io.schiar.fridgnet.library.room.relationentity.RegionWithPolygonAndHoles
import io.schiar.fridgnet.model.AdministrativeUnitLocationsGeoLocations
import io.schiar.fridgnet.model.AdministrativeLevel
import io.schiar.fridgnet.model.BoundingBox
import io.schiar.fridgnet.model.Image
import io.schiar.fridgnet.model.ImageAdministrativeUnit
import io.schiar.fridgnet.model.Location
import io.schiar.fridgnet.model.Polygon
import io.schiar.fridgnet.model.Region


fun List<AdministrativeUnitWithLocationsAndGeoLocations>.toAdministrativeUnitLocationsGeoLocations(): List<AdministrativeUnitLocationsGeoLocations> {
    return map { it.toAdministrativeUnitLocationsGeoLocations() }
}

fun List<ImageWithAdministrativeUnitAndGeoLocation>.toImageAdministrativeUnits(): List<ImageAdministrativeUnit>{
    return map { it.toImageAdministrativeUnit() }
}

fun List<ImageWithAdministrativeUnitAndGeoLocation>.toImages(): List<Image>{
    return map { it.toImage() }
}

fun AdministrativeUnitWithLocationsAndGeoLocations.toAdministrativeUnitLocationsGeoLocations(): AdministrativeUnitLocationsGeoLocations {
    return AdministrativeUnitLocationsGeoLocations(
        administrativeUnit = administrativeUnitEntity.toAdministrativeUnit(),
        administrativeLevelLocation = locationEntities.toAdministrativeLevelLocation(),
        geoLocations = geoLocationEntities.toGeoLocations()
    )
}

fun ImageWithAdministrativeUnitAndGeoLocation.toImage(): Image {
    return Image(
        uri = imageEntity.uri,
        byteArray = imageEntity.byteArray,
        date = imageEntity.date,
        geoLocation = geoLocationWithAdministrativeUnit.geoLocationEntity.toGeoLocation()
    )
}

fun ImageWithAdministrativeUnitAndGeoLocation.toImageAdministrativeUnit(): ImageAdministrativeUnit {
    return ImageAdministrativeUnit(
        image = Image(
            uri = imageEntity.uri,
            byteArray = imageEntity.byteArray,
            date = imageEntity.date,
            geoLocation = geoLocationWithAdministrativeUnit.geoLocationEntity.toGeoLocation()
        ),
        administrativeUnit = geoLocationWithAdministrativeUnit.administrativeUnitEntity?.toAdministrativeUnit()
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
        administrativeUnit = administrativeUnitEntity.toAdministrativeUnit(),
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