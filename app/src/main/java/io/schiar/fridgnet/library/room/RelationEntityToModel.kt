package io.schiar.fridgnet.library.room

import io.schiar.fridgnet.library.room.relationentity.AdministrativeUnitNameWithCartographicBoundaries
import io.schiar.fridgnet.library.room.relationentity.CartographicBoundaryWithRegions
import io.schiar.fridgnet.library.room.relationentity.ImageWithAdministrativeUnitNameAndGeoLocation
import io.schiar.fridgnet.library.room.relationentity.PolygonWithGeoLocations
import io.schiar.fridgnet.library.room.relationentity.RegionWithPolygonAndHoles
import io.schiar.fridgnet.model.AdministrativeLevel
import io.schiar.fridgnet.model.AdministrativeUnitName
import io.schiar.fridgnet.model.BoundingBox
import io.schiar.fridgnet.model.CartographicBoundary
import io.schiar.fridgnet.model.Image
import io.schiar.fridgnet.model.Polygon
import io.schiar.fridgnet.model.Region

fun List<AdministrativeUnitNameWithCartographicBoundaries>
        .toAdministrativeUnitNameWithCartographicBoundariesList()
            : List<Pair<AdministrativeUnitName, List<CartographicBoundary>>> {
    return map { it.toAdministrativeUnitNameWithCartographicBoundaries() }
}

fun List<ImageWithAdministrativeUnitNameAndGeoLocation>
        .toImageAndAdministrativeUnitNameList(): List<Pair<Image, AdministrativeUnitName?>>{
    return map { it.toImageAndAdministrativeUnitName() }
}

fun List<ImageWithAdministrativeUnitNameAndGeoLocation>.toImages(): List<Image>{
    return map { it.toImage() }
}

fun AdministrativeUnitNameWithCartographicBoundaries
        .toAdministrativeUnitNameWithCartographicBoundaries()
            : Pair<AdministrativeUnitName, List<CartographicBoundary>> {
    return Pair(
        first = administrativeUnitNameEntity.toAdministrativeUnitName(),
        second = cartographicBoundaryEntities.toCartographicBoundaries()
    )
}

fun ImageWithAdministrativeUnitNameAndGeoLocation.toImage(): Image {
    return Image(
        uri = imageEntity.uri,
        byteArray = imageEntity.byteArray,
        date = imageEntity.date,
        geoLocation = geoLocationWithAdministrativeUnitName.geoLocationEntity.toGeoLocation()
    )
}

fun ImageWithAdministrativeUnitNameAndGeoLocation
    .toImageAndAdministrativeUnitName(): Pair<Image, AdministrativeUnitName?> {
    return Pair(
        first = Image(
            uri = imageEntity.uri,
            byteArray = imageEntity.byteArray,
            date = imageEntity.date,
            geoLocation = geoLocationWithAdministrativeUnitName.geoLocationEntity.toGeoLocation()
        ),
        second = geoLocationWithAdministrativeUnitName.administrativeUnitNameEntity?.toAdministrativeUnitName()
    )
}

fun List<CartographicBoundaryWithRegions>.toCartographicBoundaries(): List<CartographicBoundary> {
    return map { it.toCartographicBoundary() }
}


fun List<CartographicBoundaryWithRegions>.toLocations(): List<CartographicBoundary> {
    return map { it.toCartographicBoundary() }
}

fun CartographicBoundaryWithRegions.toCartographicBoundary(): CartographicBoundary {
    return CartographicBoundary(
        id = cartographicBoundaryEntity.id,
        administrativeUnitName = administrativeUnitNameEntity.toAdministrativeUnitName(),
        regions = regionEntities.map { it.toRegion() },
        boundingBox = BoundingBox(
            southwest = cartographicBoundaryEntity.boundingBoxSouthwest.toGeoLocation(),
            northeast = cartographicBoundaryEntity.boundingBoxNortheast.toGeoLocation(),
        ),
        zIndex = cartographicBoundaryEntity.zIndex,
        administrativeLevel = AdministrativeLevel.valueOf(
            value = cartographicBoundaryEntity.administrativeLevel
        )
    )
}

fun PolygonWithGeoLocations.toPolygon(): Polygon {
    return Polygon(id = polygon.id, geoLocations = getLocationEntities.map { it.toGeoLocation() })
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