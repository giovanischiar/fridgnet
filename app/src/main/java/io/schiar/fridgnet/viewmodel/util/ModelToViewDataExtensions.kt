package io.schiar.fridgnet.viewmodel.util

import android.net.Uri
import io.schiar.fridgnet.model.BoundingBox
import io.schiar.fridgnet.model.Coordinate
import io.schiar.fridgnet.model.Image
import io.schiar.fridgnet.model.Location
import io.schiar.fridgnet.model.LocationCoordinate
import io.schiar.fridgnet.model.LocationImages
import io.schiar.fridgnet.model.Polygon
import io.schiar.fridgnet.model.Region
import io.schiar.fridgnet.model.mergeToBoundingBox
import io.schiar.fridgnet.view.viewdata.BoundingBoxViewData
import io.schiar.fridgnet.view.viewdata.CoordinateViewData
import io.schiar.fridgnet.view.viewdata.ImageViewData
import io.schiar.fridgnet.view.viewdata.LocationCoordinateViewData
import io.schiar.fridgnet.view.viewdata.LocationImagesViewData
import io.schiar.fridgnet.view.viewdata.LocationViewData
import io.schiar.fridgnet.view.viewdata.PolygonViewData
import io.schiar.fridgnet.view.viewdata.RegionViewData

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
    val (uri, byteArray, date, location) = this
    val (_, latitude, longitude) = location
    return ImageViewData(
        uri = Uri.parse(uri),
        byteArray = byteArray,
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
        address = addressName(),
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

// AddressLocationCoordinate
fun LocationCoordinate.toLocationImagesViewData(): LocationCoordinateViewData {
    return LocationCoordinateViewData(
        location = location?.toLocationViewData(),
        initialCoordinate = initialCoordinate?.toCoordinateViewData() ?: CoordinateViewData()
    )
}

fun List<LocationCoordinate>.toLocationCoordinateViewDataList()
        : List<LocationCoordinateViewData> {
    return map { it.toLocationImagesViewData() }
}

// AddressLocationImages
fun LocationImages.toLocationImagesViewData(): LocationImagesViewData {
    return LocationImagesViewData(
        location = location.toLocationViewData(),
        images = images.toImageViewDataList(),
        imagesBoundingBox = images.mergeToBoundingBox()?.toBoundingBoxViewData()
    )
}