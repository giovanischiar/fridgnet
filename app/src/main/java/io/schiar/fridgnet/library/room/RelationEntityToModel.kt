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

/**
 * Converts a [List] of [ImageWithAdministrativeUnitNameAndGeoLocation] objects to a list of [Image]
 * objects.
 *
 * This method extracts the core [Image] data from each
 * [ImageWithAdministrativeUnitNameAndGeoLocation] object using the toImage() method.
 *
 * @return a new [List] containing the converted [Image] objects.
 */
fun List<ImageWithAdministrativeUnitNameAndGeoLocation>.toImages(): List<Image>{
    return map { it.toImage() }
}

/**
 * Converts a [AdministrativeUnitNameWithCartographicBoundaries] object into a pair containing
 * the corresponding [AdministrativeUnitName] and a list of its associated [CartographicBoundary]
 * objects.
 *
 * This function unpacks the data from a single [AdministrativeUnitNameWithCartographicBoundaries]
 * object into a more convenient structure for separate access to the administrative unit name
 * and its cartographic boundaries.
 *
 * @return A [Pair] containing the extracted [AdministrativeUnitName] and a [List] of
 * [CartographicBoundary]` objects.
 */
fun AdministrativeUnitNameWithCartographicBoundaries
        .toAdministrativeUnitNameAndCartographicBoundaries()
            : Pair<AdministrativeUnitName, List<CartographicBoundary>> {
    return Pair(
        first = administrativeUnitNameEntity.toAdministrativeUnitName(),
        second = cartographicBoundaryEntities.toCartographicBoundaries()
    )
}

/**
 * Converts [ImageWithAdministrativeUnitNameAndGeoLocation] object to an [Image] object.
 *
 * This method extracts the core [Image] data from each
 * [ImageWithAdministrativeUnitNameAndGeoLocation]
 *
 * @return The converted [Image] object.
 */
fun ImageWithAdministrativeUnitNameAndGeoLocation.toImage(): Image {
    return Image(
        uri = imageEntity.uri,
        byteArray = imageEntity.byteArray,
        date = imageEntity.date,
        geoLocation = geoLocationWithAdministrativeUnitName.geoLocationEntity.toGeoLocation()
    )
}

/**
 * Converts a [ImageWithAdministrativeUnitNameAndGeoLocation] into a pair of Image and a
 * AdministrativeUnitName if existent.
 *
 * @return the Pair containing the [Image] and the optional [AdministrativeUnitName]
 */
fun ImageWithAdministrativeUnitNameAndGeoLocation
    .toImageAndAdministrativeUnitName(): Pair<Image, AdministrativeUnitName?> {
    return Pair(
        first = Image(
            uri = imageEntity.uri,
            byteArray = imageEntity.byteArray,
            date = imageEntity.date,
            geoLocation = geoLocationWithAdministrativeUnitName.geoLocationEntity.toGeoLocation()
        ),
        second = geoLocationWithAdministrativeUnitName.administrativeUnitNameEntity
            ?.toAdministrativeUnitName()
    )
}

/**
 * Converts a list of CartographicBoundaryWithRegions objects to a list of CartographicBoundary
 * objects.
 *
 * This method extracts the core CartographicBoundary data from each CartographicBoundaryWithRegions
 * object using the toCartographicBoundary() method.
 *
 * @return a new List containing the converted CartographicBoundary objects.
 */
fun List<CartographicBoundaryWithRegions>.toCartographicBoundaries(): List<CartographicBoundary> {
    return map { it.toCartographicBoundary() }
}

/**
 * Converts [CartographicBoundaryWithRegions] object to a [CartographicBoundary] object.
 *
 * This method extracts the core [CartographicBoundary] data from each
 * [CartographicBoundaryWithRegions] object
 *
 * @return The converted CartographicBoundary object.
 */
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

/**
 * Converts a [PolygonWithGeoLocations] object into a [Polygon] object
 *
 * @return the [Polygon] converted
 */
fun PolygonWithGeoLocations.toPolygon(): Polygon {
    return Polygon(id = polygon.id, geoLocations = getLocationEntities.map { it.toGeoLocation() })
}

/**
 * Converts a [RegionWithPolygonAndHoles] object into a [Region] object
 *
 * @return the [Region] converted
 */
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