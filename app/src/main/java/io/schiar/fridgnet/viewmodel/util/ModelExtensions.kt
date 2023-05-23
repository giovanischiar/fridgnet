package io.schiar.fridgnet.viewmodel.util

import android.net.Uri
import io.schiar.fridgnet.model.*
import io.schiar.fridgnet.view.viewdata.*

// BoundingBox
fun BoundingBox.toBoundingBoxViewData(): BoundingBoxViewData {
    return BoundingBoxViewData(
        southwest = southwest.toCoordinateViewData(),
        northeast = northeast.toCoordinateViewData()
    )
}

// Coordinate
fun Coordinate.toCoordinateViewData(): CoordinateViewData {
    return CoordinateViewData(
        latitude = latitude,
        longitude = longitude
    )
}

// Image
fun Image.toViewData(): ImageViewData {
    val (uri, date, location) = this
    val (latitude, longitude) = location
    return ImageViewData(
        uri = Uri.parse(uri),
        date = date.toString(),
        coordinate = CoordinateViewData(
            latitude = latitude,
            longitude = longitude,
        )
    )
}

fun List<Image>.toImageViewDataList(): List<ImageViewData> {
    return map { it.toViewData() }
}

// Location
fun Location.toLocationViewData(): LocationViewData {
    return LocationViewData(
        address = address.name(),
        regions = regions.map { it.toRegionViewData() },
        boundingBox = boundingBox.toBoundingBoxViewData(),
        center = boundingBox.center().toCoordinateViewData(),
        zIndex = zIndex
    )
}

// Polygon
fun Polygon.toPolygonViewData(): PolygonViewData {
    return PolygonViewData(coordinates = this.coordinates.map { it.toCoordinateViewData() })
}

// Region
fun Region.toRegionViewData(): RegionViewData {
    return RegionViewData(
        polygon = polygon.toPolygonViewData(),
        holes = holes.map { it.toPolygonViewData() },
        active = active,
        boundingBox = boundingBox.toBoundingBoxViewData(),
        center = boundingBox.center().toCoordinateViewData(),
        zIndex = zIndex
    )
}

fun List<Region>.toRegionViewDataList(): List<RegionViewData> {
    return map { it.toRegionViewData() }
}

// view.util.AddressCreator
fun android.location.Address.toAddress(): Address {
    return Address(
        locality = locality,
        subAdminArea = subAdminArea,
        adminArea = adminArea,
        countryName = countryName
    )
}

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

// view
fun Map<String, Image>.toImageViewData(): List<ImageViewData> {
    return values.toList().toImageViewDataList()
}

fun Map<String, List<Image>>.toStringImageViewDataList(): Map<String, List<ImageViewData>> {
    return mapValues { it.value.toImageViewDataList() }
}

fun Map<String, Location>.toStringLocationViewData(): Map<String, LocationViewData> {
    return mapValues { it.value.toLocationViewData() }
}