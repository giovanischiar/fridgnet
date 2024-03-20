package io.schiar.fridgnet.library.room

import io.schiar.fridgnet.library.room.relationentity.AdministrativeUnitWithCartographicBoundaries
import io.schiar.fridgnet.library.room.relationentity.CartographicBoundaryWithRegions
import io.schiar.fridgnet.library.room.relationentity.ImageWithAdministrativeUnitAndGeoLocation
import io.schiar.fridgnet.library.room.relationentity.PolygonWithGeoLocations
import io.schiar.fridgnet.library.room.relationentity.RegionWithPolygonAndHoles
import io.schiar.fridgnet.model.AdministrativeLevel
import io.schiar.fridgnet.model.AdministrativeUnit
import io.schiar.fridgnet.model.BoundingBox
import io.schiar.fridgnet.model.CartographicBoundary
import io.schiar.fridgnet.model.Image
import io.schiar.fridgnet.model.Polygon
import io.schiar.fridgnet.model.Region

fun List<AdministrativeUnitWithCartographicBoundaries>
        .toAdministrativeUnitWithCartographicBoundariesList()
            : List<Pair<AdministrativeUnit, List<CartographicBoundary>>> {
    return map { it.toAdministrativeUnitWithCartographicBoundaries() }
}

fun List<ImageWithAdministrativeUnitAndGeoLocation>
        .toAdministrativeUnitAndImageList(): List<Pair<AdministrativeUnit?, Image>>{
    return map { it.toAdministrativeUnitAndImage() }
}

fun List<ImageWithAdministrativeUnitAndGeoLocation>.toImages(): List<Image>{
    return map { it.toImage() }
}

fun AdministrativeUnitWithCartographicBoundaries
        .toAdministrativeUnitWithCartographicBoundaries()
            : Pair<AdministrativeUnit, List<CartographicBoundary>> {
    return Pair(
        first = administrativeUnitEntity.toAdministrativeUnit(),
        second = cartographicBoundaryEntities.toCartographicBoundaries()
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

fun ImageWithAdministrativeUnitAndGeoLocation
    .toAdministrativeUnitAndImage(): Pair<AdministrativeUnit?, Image> {
    return Pair(
        first = geoLocationWithAdministrativeUnit.administrativeUnitEntity?.toAdministrativeUnit(),
        second = Image(
            uri = imageEntity.uri,
            byteArray = imageEntity.byteArray,
            date = imageEntity.date,
            geoLocation = geoLocationWithAdministrativeUnit.geoLocationEntity.toGeoLocation()
        )
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
        administrativeUnit = administrativeUnitEntity.toAdministrativeUnit(),
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