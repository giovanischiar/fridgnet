package io.schiar.fridgnet.viewmodel.util

import android.net.Uri
import io.schiar.fridgnet.model.AdministrativeUnit
import io.schiar.fridgnet.model.BoundingBox
import io.schiar.fridgnet.model.CartographicBoundary
import io.schiar.fridgnet.model.GeoLocation
import io.schiar.fridgnet.model.Image
import io.schiar.fridgnet.model.Polygon
import io.schiar.fridgnet.model.Region
import io.schiar.fridgnet.model.mergeToBoundingBox
import io.schiar.fridgnet.view.viewdata.AdministrativeUnitViewData
import io.schiar.fridgnet.view.viewdata.BoundingBoxViewData
import io.schiar.fridgnet.view.viewdata.CartographicBoundaryViewData
import io.schiar.fridgnet.view.viewdata.GeoLocationViewData
import io.schiar.fridgnet.view.viewdata.ImageViewData
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

// CartographicBoundary
fun CartographicBoundary.toCartographicBoundaryViewData(): CartographicBoundaryViewData {
    return CartographicBoundaryViewData(
        administrativeUnitName = administrativeUnitNameString(),
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

// AdministrativeUnit
fun AdministrativeUnit.toAdministrativeUnitViewData(): AdministrativeUnitViewData {
    return AdministrativeUnitViewData(
        name = name,
        administrativeLevel = administrativeLevel.toString(),
        cartographicBoundary = cartographicBoundary?.toCartographicBoundaryViewData(),
        subAdministrativeUnitNames = subAdministrativeUnitNames.toAdministrativeUnitViewDataList(),
        images = images.toImageViewDataList(),
        imagesBoundingBox = images.mergeToBoundingBox()?.toBoundingBoxViewData()
    )
}

// AdministrativeUnitList
fun List<AdministrativeUnit>.toAdministrativeUnitViewDataList(): List<AdministrativeUnitViewData> {
    return map { it.toAdministrativeUnitViewData() }
}