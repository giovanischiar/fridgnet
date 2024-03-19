package io.schiar.fridgnet.library.retrofit

import io.schiar.fridgnet.model.Address
import io.schiar.fridgnet.model.AdministrativeLevel
import io.schiar.fridgnet.model.BoundingBox
import io.schiar.fridgnet.model.GeoLocation
import io.schiar.fridgnet.model.Location
import io.schiar.fridgnet.model.Polygon
import io.schiar.fridgnet.model.Region

fun List<Double>.toGeoLocation(): GeoLocation {
    return GeoLocation(latitude = this[1], longitude = this[0])
}

fun List<List<Double>>.toLineStringGeoLocations(): List<GeoLocation> {
    return map { it.toGeoLocation() }
}

fun List<List<List<Double>>>.toPolygonGeoLocations(): List<List<GeoLocation>> {
    return map { it.toLineStringGeoLocations() }
}

fun List<List<List<List<Double>>>>.toMultiPolygonGeoLocations(): List<List<List<GeoLocation>>> {
    return map { it.toPolygonGeoLocations() }
}

fun List<String>.toBoundingBox(): BoundingBox {
    val southwest = GeoLocation(latitude = this[0].toDouble(), longitude = this[2].toDouble())
    val northeast = GeoLocation(latitude = this[1].toDouble(), longitude = this[3].toDouble())
    return BoundingBox(southwest = southwest, northeast = northeast)
}

fun JSONResult<GeoJSONAttributes>.toLocation(
    address: Address, administrativeLevel: AdministrativeLevel
): Location? {
    val regions = when (geoJSON.type) {
        "Point" -> {
            val pointDoubleList = geoJSON.coordinates as List<Double>
            val polygon = Polygon(geoLocations = listOf(pointDoubleList.toGeoLocation()))
            val region = Region(
                polygon = polygon,
                holes = emptyList(),
                boundingBox = polygon.findBoundingBox(),
                zIndex = administrativeLevel.zIndex()
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
                zIndex = administrativeLevel.zIndex()
            )
            listOf(region)
        }

        "Polygon" -> {
            val pointDoubleList = geoJSON.coordinates as List<List<List<Double>>>
            val polygonGeoLocations = pointDoubleList.toPolygonGeoLocations()
            val polygon = Polygon(geoLocations = polygonGeoLocations[0])
            val region = Region(
                polygon = polygon,
                holes = polygonGeoLocations.subList(1, polygonGeoLocations.size).map {
                    Polygon(geoLocations = it)
                },
                boundingBox = polygon.findBoundingBox(),
                zIndex = administrativeLevel.zIndex()
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
                    zIndex = administrativeLevel.zIndex()
                )
            }
            regions
        }

        else -> return null
    }

    return Location(
        address = address,
        regions = regions,
        boundingBox = boundingBox.toBoundingBox(),
        zIndex = administrativeLevel.zIndex(),
        administrativeLevel = administrativeLevel
    )
}