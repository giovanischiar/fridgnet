package io.schiar.fridgnet.viewmodel.util

import android.net.Uri
import io.schiar.fridgnet.model.BoundingBox
import io.schiar.fridgnet.model.GeoLocation
import io.schiar.fridgnet.model.Image
import io.schiar.fridgnet.model.Location
import io.schiar.fridgnet.model.LocationGeoLocation
import io.schiar.fridgnet.model.LocationImages
import io.schiar.fridgnet.model.Polygon
import io.schiar.fridgnet.model.Region
import io.schiar.fridgnet.model.mergeToBoundingBox
import io.schiar.fridgnet.view.viewdata.BoundingBoxViewData
import io.schiar.fridgnet.view.viewdata.GeoLocationViewData
import io.schiar.fridgnet.view.viewdata.ImageViewData
import io.schiar.fridgnet.view.viewdata.LocationGeoLocationViewData
import io.schiar.fridgnet.view.viewdata.LocationImagesViewData
import io.schiar.fridgnet.view.viewdata.LocationViewData
import io.schiar.fridgnet.view.viewdata.PolygonViewData
import io.schiar.fridgnet.view.viewdata.RegionViewData

// BoundingBox
fun BoundingBox.toBoundingBoxViewData(): BoundingBoxViewData {
    return BoundingBoxViewData(
        southwest = southwest.toGeoLocationViewData(),
        northeast = northeast.toGeoLocationViewData()
    )
}

// GeoLocation
fun GeoLocation.toGeoLocationViewData(): GeoLocationViewData {
    return GeoLocationViewData(
        latitude = latitude,
        longitude = longitude
    )
}

// Image
fun Image.toViewData(): ImageViewData {
    val (uri, byteArray, date, geoLocation) = this
    val (_, latitude, longitude) = geoLocation
    return ImageViewData(
        uri = Uri.parse(uri),
        byteArray = byteArray,
        date = date.toString(),
        geoLocation = GeoLocationViewData(
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
        center = boundingBox.center().toGeoLocationViewData(),
        zIndex = zIndex
    )
}

// Polygon
fun Polygon.toPolygonViewData(): PolygonViewData {
    return PolygonViewData(geoLocations = this.geoLocations.map { it.toGeoLocationViewData() })
}

// Region
fun Region.toRegionViewData(): RegionViewData {
    return RegionViewData(
        polygon = polygon.toPolygonViewData(),
        holes = holes.map { it.toPolygonViewData() },
        active = active,
        boundingBox = boundingBox.toBoundingBoxViewData(),
        center = boundingBox.center().toGeoLocationViewData(),
        zIndex = zIndex
    )
}

fun List<Region>.toRegionViewDataList(): List<RegionViewData> {
    return map { it.toRegionViewData() }
}

// LocationGeoLocation
fun LocationGeoLocation.toLocationImagesViewData(): LocationGeoLocationViewData {
    return LocationGeoLocationViewData(
        location = location?.toLocationViewData(),
        initialGeoLocation = initialGeoLocation?.toGeoLocationViewData() ?: GeoLocationViewData()
    )
}

fun List<LocationGeoLocation>.toLocationGeoLocationViewDataList()
        : List<LocationGeoLocationViewData> {
    return map { it.toLocationImagesViewData() }
}

// LocationImages
fun LocationImages.toLocationImagesViewData(): LocationImagesViewData {
    return LocationImagesViewData(
        location = location.toLocationViewData(),
        images = images.toImageViewDataList(),
        imagesBoundingBox = images.mergeToBoundingBox()?.toBoundingBoxViewData()
    )
}