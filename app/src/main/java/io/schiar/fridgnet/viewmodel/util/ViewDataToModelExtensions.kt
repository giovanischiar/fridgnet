package io.schiar.fridgnet.viewmodel.util

import io.schiar.fridgnet.model.BoundingBox
import io.schiar.fridgnet.model.Coordinate
import io.schiar.fridgnet.model.Polygon
import io.schiar.fridgnet.model.Region
import io.schiar.fridgnet.view.viewdata.BoundingBoxViewData
import io.schiar.fridgnet.view.viewdata.CoordinateViewData
import io.schiar.fridgnet.view.viewdata.PolygonViewData
import io.schiar.fridgnet.view.viewdata.RegionViewData

//view.viewdata.CoordinateViewData
fun CoordinateViewData.toCoordinate(): Coordinate {
    return Coordinate(latitude = latitude, longitude = longitude)
}

fun List<CoordinateViewData>.toCoordinateList(): List<Coordinate> {
    return map { it.toCoordinate() }
}

// view.viewdata.BoundingBoxViewData
fun BoundingBoxViewData.toBoundingBox(): BoundingBox {
    return BoundingBox(southwest = southwest.toCoordinate(), northeast = northeast.toCoordinate())
}

// view.viewdata.PolygonViewData
fun PolygonViewData.toPolygon(): Polygon {
    return Polygon(coordinates = coordinates.toCoordinateList())
}

fun List<PolygonViewData>.toPolygonList(): List<Polygon> {
    return map { it.toPolygon() }
}

// view.viewdata.RegionViewData
fun RegionViewData.toRegion(): Region {
    return Region(
        polygon = polygon.toPolygon(),
        holes = holes.toPolygonList(),
        active = active,
        boundingBox = boundingBox.toBoundingBox(),
        zIndex = zIndex
    )
}