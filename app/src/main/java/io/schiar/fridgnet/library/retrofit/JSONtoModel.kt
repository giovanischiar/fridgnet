package io.schiar.fridgnet.library.retrofit

import io.schiar.fridgnet.model.AdministrativeLevel
import io.schiar.fridgnet.model.AdministrativeUnitName
import io.schiar.fridgnet.model.BoundingBox
import io.schiar.fridgnet.model.CartographicBoundary
import io.schiar.fridgnet.model.GeoLocation
import io.schiar.fridgnet.model.Polygon
import io.schiar.fridgnet.model.Region

/**
 * Converts a list of coordinates (assumed to be longitude first, latitude second) returned by the
 * API to a [GeoLocation] object.
 *
 * Note that the order of coordinates is swapped in the [GeoLocation] object, where latitude comes
 * first.
 *
 * @return A `GeoLocation` object with the coordinates reordered (latitude first, longitude second).
 */
fun List<Double>.toGeoLocation(): GeoLocation {
    return GeoLocation(latitude = this[1], longitude = this[0])
}

/**
 * Convert a [List] of a [List] of coordinates into a list of [GeoLocation]
 *
 * @return a [List] of [GeoLocation] containing the converted [GeoLocation] objects.
 */
fun List<List<Double>>.toLineStringGeoLocations(): List<GeoLocation> {
    return map { it.toGeoLocation() }
}

/**
 * Converts a list of lists of lists of coordinates into a list of lists of GeoLocation objects,
 * representing a polygon.
 *
 * @return A List<List<GeoLocation>> containing the converted GeoLocation objects for each polygon.
 */
fun List<List<List<Double>>>.toPolygonGeoLocations(): List<List<GeoLocation>> {
    return map { it.toLineStringGeoLocations() }
}

/**
 * Converts a list of lists of lists of lists of coordinates into a list of lists of lists of
 * GeoLocation objects, representing a multipolygon.
 *
 * @return A List<List<List<GeoLocation>>> containing the converted GeoLocation objects for each
 * multipolygon.
 */
fun List<List<List<List<Double>>>>.toMultiPolygonGeoLocations(): List<List<List<GeoLocation>>> {
    return map { it.toPolygonGeoLocations() }
}

/**
 * Converts a list of four strings representing a bounding box in the order:
 *  * southwest longitude
 *  * southwest latitude
 *  * northeast longitude
 *  * northeast latitude
 *
 * into a [BoundingBox] object.
 *
 * @return the [BoundingBox] object.
 */
fun List<String>.toBoundingBox(): BoundingBox {
    val southwest = GeoLocation(latitude = this[0].toDouble(), longitude = this[2].toDouble())
    val northeast = GeoLocation(latitude = this[1].toDouble(), longitude = this[3].toDouble())
    return BoundingBox(southwest = southwest, northeast = northeast)
}

/**
 * Converts a [JSONResult] object containing GeoJSON data into a [CartographicBoundary] object.
 * A [CartographicBoundary] represents the geographic boundary of an administrative unit
 * (e.g., country, state, county).
 *
 * @param administrativeUnitName the [AdministrativeUnitName] used
 * @param administrativeLevel the [AdministrativeLevel] used
 *
 * @return the [CartographicBoundary] object or null if the GeoJSON type is unexpected.
 */
fun JSONResult<GeoJSONAttributes>.toCartographicBoundary(
    administrativeUnitName: AdministrativeUnitName, administrativeLevel: AdministrativeLevel
): CartographicBoundary? {
    val regions = when (geoJSON.type) {
        "Point" -> {
            val pointDoubleList = geoJSON.coordinates as List<Double>
            val polygon = Polygon(geoLocations = listOf(pointDoubleList.toGeoLocation()))
            val region = Region(
                polygon = polygon,
                holes = emptyList(),
                boundingBox = polygon.findBoundingBox(),
                zIndex = administrativeLevel.zIndex
            )
            listOf(region)
        }

        "LineString" -> {
            val pointDoubleList = geoJSON.coordinates as List<List<Double>>
            val polygon = Polygon(geoLocations = pointDoubleList.toLineStringGeoLocations())
            val region = Region(
                polygon = polygon,
                holes = emptyList(),
                boundingBox = polygon.findBoundingBox(),
                zIndex = administrativeLevel.zIndex
            )
            listOf(region)
        }

        "Polygon" -> {
            val pointDoubleList = geoJSON.coordinates as List<List<List<Double>>>
            val polygonGeoLocations = pointDoubleList.toPolygonGeoLocations()
            val polygon = Polygon(geoLocations = polygonGeoLocations[0])
            val region = Region(
                polygon = polygon,
                holes = polygonGeoLocations.subList(
                    1, polygonGeoLocations.size
                ).map { geoLocations -> Polygon(geoLocations = geoLocations) },
                boundingBox = polygon.findBoundingBox(),
                zIndex = administrativeLevel.zIndex
            )
            listOf(region)
        }

        "MultiPolygon" -> {
            val pointDoubleList = geoJSON.coordinates as List<List<List<List<Double>>>>
            val regions = pointDoubleList.toMultiPolygonGeoLocations().map {
                val polygon = Polygon(geoLocations = it[0])
                Region(
                    polygon = polygon,
                    holes = it.subList(1, it.size).map { geoLocations ->
                        Polygon(geoLocations = geoLocations)
                    },
                    boundingBox = polygon.findBoundingBox(),
                    zIndex = administrativeLevel.zIndex
                )
            }
            regions
        }

        else -> return null
    }

    return CartographicBoundary(
        administrativeUnitName = administrativeUnitName,
        regions = regions,
        boundingBox = boundingBox.toBoundingBox(),
        zIndex = administrativeLevel.zIndex,
        administrativeLevel = administrativeLevel
    )
}