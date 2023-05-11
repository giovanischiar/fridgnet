package io.schiar.fridgnet.viewmodel.util

import android.net.Uri
import io.schiar.fridgnet.model.*
import io.schiar.fridgnet.view.viewdata.*

// BoundingBox
fun BoundingBox.toBoundingBoxViewData(): BoundingBoxViewData {
    return BoundingBoxViewData(
        northeast = this.northeast.toCoordinateViewData(),
        southwest = this.southwest.toCoordinateViewData()
    )
}

// Coordinate
fun Coordinate.toCoordinateViewData(): CoordinateViewData {
    return CoordinateViewData(
        latitude = this.latitude,
        longitude = this.longitude
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
        regions = this.regions.map { it.toRegionViewData() },
        boundingBox = this.boundingBox.toBoundingBoxViewData()
    )
}

//Polygon
fun Polygon.toPolygonViewData(): PolygonViewData {
    return PolygonViewData(coordinates = this.coordinates.map { it.toCoordinateViewData() })
}

//Region
fun Region.toRegionViewData(): RegionViewData {
    return RegionViewData(
        polygon = this.polygon.toPolygonViewData(),
        holes = this.holes.map { it.toPolygonViewData() },
        active = this.active,
        boundingBox = this.boundingBox.toBoundingBoxViewData()
    )
}

// view.util.AddressCreator
fun android.location.Address.toAddress(): Address {
    return Address(
        locality = this.locality,
        subAdminArea = this.subAdminArea,
        adminArea = this.adminArea,
        countryName = this.countryName
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

fun Map<String, Map<String, Location>>.toStringStringLocationViewData(): Map<String, Map<String, LocationViewData>> {
    return this.mapValues { it.value.toStringLocationViewData() }
}