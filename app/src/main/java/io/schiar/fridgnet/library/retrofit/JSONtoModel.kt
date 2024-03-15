package io.schiar.fridgnet.library.retrofit

import io.schiar.fridgnet.model.Address
import io.schiar.fridgnet.model.AdministrativeUnit
import io.schiar.fridgnet.model.BoundingBox
import io.schiar.fridgnet.model.Coordinate
import io.schiar.fridgnet.model.Location
import io.schiar.fridgnet.model.Polygon
import io.schiar.fridgnet.model.Region

fun List<Double>.toCoordinate(): Coordinate {
    return Coordinate(latitude = this[1], longitude = this[0])
}

fun List<List<Double>>.toLineStringCoordinates(): List<Coordinate> {
    return map { it.toCoordinate() }
}

fun List<List<List<Double>>>.toPolygonCoordinates(): List<List<Coordinate>> {
    return map { it.toLineStringCoordinates() }
}

fun List<List<List<List<Double>>>>.toMultiPolygonCoordinates(): List<List<List<Coordinate>>> {
    return map { it.toPolygonCoordinates() }
}

fun List<String>.toBoundingBox(): BoundingBox {
    val southwest = Coordinate(latitude = this[0].toDouble(), longitude = this[2].toDouble())
    val northeast = Coordinate(latitude = this[1].toDouble(), longitude = this[3].toDouble())
    return BoundingBox(southwest = southwest, northeast = northeast)
}

fun JSONResult<GeoJSONAttributes>.toLocation(
    address: Address, administrativeUnit: AdministrativeUnit
): Location? {
    val regions = when (geoJSON.type) {
        "Point" -> {
            val pointDoubleList = geoJSON.coordinates as List<Double>
            val polygon = Polygon(coordinates = listOf(pointDoubleList.toCoordinate()))
            val region = Region(
                polygon = polygon,
                holes = emptyList(),
                boundingBox = polygon.findBoundingBox(),
                zIndex = administrativeUnit.zIndex()
            )
            listOf(region)
        }

        "LineString" -> {
            val pointDoubleList = geoJSON.coordinates as List<List<Double>>
            val polygon = Polygon(coordinates = pointDoubleList.toLineStringCoordinates())
            val region = Region(
                polygon = polygon,
                holes = emptyList(),
                boundingBox = polygon.findBoundingBox(),
                zIndex = administrativeUnit.zIndex()
            )
            listOf(region)
        }

        "Polygon" -> {
            val pointDoubleList = geoJSON.coordinates as List<List<List<Double>>>
            val polygonCoordinates = pointDoubleList.toPolygonCoordinates()
            val polygon = Polygon(coordinates = polygonCoordinates[0])
            val region = Region(
                polygon = polygon,
                holes = polygonCoordinates.subList(1, polygonCoordinates.size).map {
                    Polygon(coordinates = it)
                },
                boundingBox = polygon.findBoundingBox(),
                zIndex = administrativeUnit.zIndex()
            )
            listOf(region)
        }

        "MultiPolygon" -> {
            val pointDoubleList = geoJSON.coordinates as List<List<List<List<Double>>>>
            val regions = pointDoubleList.toMultiPolygonCoordinates().map {
                val polygon = Polygon(coordinates = it[0])
                Region(
                    polygon = polygon,
                    holes = it.subList(1, it.size).map { coordinates ->
                        Polygon(coordinates = coordinates)
                    },
                    boundingBox = polygon.findBoundingBox(),
                    zIndex = administrativeUnit.zIndex()
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
        zIndex = administrativeUnit.zIndex(),
        administrativeUnit = administrativeUnit
    )
}