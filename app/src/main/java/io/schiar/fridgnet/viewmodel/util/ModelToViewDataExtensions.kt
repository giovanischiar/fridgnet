package io.schiar.fridgnet.viewmodel.util

import io.schiar.fridgnet.Log
import io.schiar.fridgnet.model.AdministrativeLevel
import io.schiar.fridgnet.model.AdministrativeUnit
import io.schiar.fridgnet.model.BoundingBox
import io.schiar.fridgnet.model.CartographicBoundary
import io.schiar.fridgnet.model.GeoLocation
import io.schiar.fridgnet.model.Image
import io.schiar.fridgnet.model.Polygon
import io.schiar.fridgnet.model.Region
import io.schiar.fridgnet.model.mergeToBoundingBox
import io.schiar.fridgnet.view.shared.viewdata.AdministrativeLevelViewData
import io.schiar.fridgnet.view.shared.viewdata.AdministrativeUnitViewData
import io.schiar.fridgnet.view.shared.viewdata.BoundingBoxViewData
import io.schiar.fridgnet.view.shared.viewdata.CartographicBoundaryViewData
import io.schiar.fridgnet.view.shared.viewdata.GeoLocationViewData
import io.schiar.fridgnet.view.shared.viewdata.ImageViewData
import io.schiar.fridgnet.view.shared.viewdata.PolygonViewData
import io.schiar.fridgnet.view.shared.viewdata.RegionViewData

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
        uri = uri,
        byteArray = byteArray,
        date = date.toString(),
        geoLocation = GeoLocationViewData(
            latitude = latitude,
            longitude = longitude,
        )
    )
}

fun Collection<Image>.toImageViewDataList(): List<ImageViewData> {
    return map { it.toViewData() }
}

// CartographicBoundary
fun CartographicBoundary.toCartographicBoundaryViewData(): CartographicBoundaryViewData {
    return CartographicBoundaryViewData(
        administrativeUnitName = administrativeUnitNameString,
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
    if (administrativeLevel == AdministrativeLevel.CITY && subAdministrativeUnits.isNotEmpty()) {
        Log.d("", "WTF")
    }
    return AdministrativeUnitViewData(
        name = name,
        administrativeLevel = administrativeLevel.toAdministrativeUnitLevelViewData(),
        cartographicBoundary = cartographicBoundary?.toCartographicBoundaryViewData(),
        subCartographicBoundaries = flatMapSubCartographicBoundariesViewDataList(
            administrativeUnit = this, mutableListOf()
        ),
        images = images.toImageViewDataList(),
        imagesBoundingBox = images.mergeToBoundingBox()?.toBoundingBoxViewData()
    )
}

// AdministrativeUnitList
fun Collection<AdministrativeUnit>
        .toAdministrativeUnitViewDataList(): List<AdministrativeUnitViewData> {
    return map { it.toAdministrativeUnitViewData() }
}

// AdministrativeLevel
fun AdministrativeLevel.toAdministrativeUnitLevelViewData(): AdministrativeLevelViewData {
    return AdministrativeLevelViewData(
        title = name,
        columnCount = administrativeUnitSize,
        zIndex = zIndex
    )
}

fun List<AdministrativeLevel>
        .toAdministrativeLevelViewDataList(): List<AdministrativeLevelViewData> {
    return map { it.toAdministrativeUnitLevelViewData() }
}

fun AdministrativeUnit.flatMapSubCartographicBoundariesViewDataList(
    administrativeUnit: AdministrativeUnit,
    cartographicBoundaries: MutableList<CartographicBoundaryViewData>
): List<CartographicBoundaryViewData> {
    val subCartographicBoundary = administrativeUnit.cartographicBoundary
    if (subCartographicBoundary != null) {
        cartographicBoundaries.add(subCartographicBoundary.toCartographicBoundaryViewData())
    }

    for (subAdministrativeUnit in administrativeUnit.subAdministrativeUnits) {
        flatMapSubCartographicBoundariesViewDataList(subAdministrativeUnit, cartographicBoundaries)
    }
    return cartographicBoundaries
}

