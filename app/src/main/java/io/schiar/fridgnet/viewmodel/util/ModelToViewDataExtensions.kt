package io.schiar.fridgnet.viewmodel.util

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

/**
 * Converts the [BoundingBox] model into the view object [BoundingBoxViewData] used to represent
 * bounding boxes in the UI.
 *
 * @return the [BoundingBoxViewData] converted
 */
fun BoundingBox.toBoundingBoxViewData(): BoundingBoxViewData {
    return BoundingBoxViewData(
        southwest = southwest.toGeoLocationViewData(),
        northeast = northeast.toGeoLocationViewData()
    )
}

/**
 * Converts the [GeoLocation] model into the view object [GeoLocationViewData] used to represent
 * geo locations in the UI.
 *
 * @return the [GeoLocationViewData] converted
 */
fun GeoLocation.toGeoLocationViewData(): GeoLocationViewData {
    return GeoLocationViewData(
        latitude = latitude,
        longitude = longitude
    )
}

/**
 * Converts the [Image] model into the view object [ImageViewData] used to represent
 * images in the UI.
 *
 * @return the [ImageViewData] converted
 */
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

/**
 * Converts a collection of [Image] models into a list of [ImageViewData] view objects used to
 * represent  a list of images in the UI. Each [Image] is converted to an [ImageViewData] object
 * using its `toViewData` method, which likely extracts relevant data for UI display.
 *
 * @return the [List] of [ImageViewData] converted
 */
fun Collection<Image>.toImageViewDataList(): List<ImageViewData> {
    return map { it.toViewData() }
}

/**
 * Converts the [CartographicBoundary] model into the view object [CartographicBoundaryViewData]
 * used to represent cartographic boundaries in the UI.
 *
 * @return the [CartographicBoundaryViewData] converted
 */
fun CartographicBoundary.toCartographicBoundaryViewData(): CartographicBoundaryViewData {
    return CartographicBoundaryViewData(
        administrativeUnitName = administrativeUnitNameString,
        regions = regions.map { it.toRegionViewData() },
        boundingBox = boundingBox.toBoundingBoxViewData(),
        center = boundingBox.center().toGeoLocationViewData(),
        zIndex = zIndex
    )
}

/**
 * Converts the [Polygon] model into the view object [PolygonViewData]
 * used to represent polygons in the UI.
 *
 * @return the [PolygonViewData] converted
 */
fun Polygon.toPolygonViewData(): PolygonViewData {
    return PolygonViewData(geoLocations = this.geoLocations.map { it.toGeoLocationViewData() })
}

/**
 * Converts the [Region] model into the view object [RegionViewData]
 * used to represent polygons in the UI.
 *
 * @return the [RegionViewData] converted
 */
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

/**
 * Converts a collection of [Region] models into a list of [RegionViewData] view objects used to
 * represent  a list of regions in the UI. Each [Region] is converted to an [RegionViewData] object
 * using its `toRegionViewData` method, which likely extracts relevant data for UI display.
 *
 * @return the [List] of [RegionViewData] converted
 */
fun List<Region>.toRegionViewDataList(): List<RegionViewData> {
    return map { it.toRegionViewData() }
}

/**
 * Converts the [AdministrativeUnit] model into the view object [AdministrativeUnitViewData]
 * used to represent administrative units in the UI.
 *
 * @return the [AdministrativeUnitViewData] converted
 */
fun AdministrativeUnit.toAdministrativeUnitViewData(): AdministrativeUnitViewData {
    return AdministrativeUnitViewData(
        name = name,
        administrativeLevel = administrativeLevel.toAdministrativeUnitLevelViewData(),
        cartographicBoundary = cartographicBoundary?.toCartographicBoundaryViewData(),
        subCartographicBoundaries = flatMapSubCartographicBoundariesViewDataList(
            administrativeUnit = this
        ),
        images = images.toImageViewDataList(),
        imagesBoundingBox = images.mergeToBoundingBox()?.toBoundingBoxViewData()
    )
}

/**
 * Converts a collection of [AdministrativeUnit] models into a list of [AdministrativeUnitViewData]
 * view objects used to represent  a list of administrative units in the UI. Each
 * [AdministrativeUnit] is converted to an [AdministrativeUnitViewData] object
 * using its `toAdministrativeUnitViewData` method, which likely extracts relevant data for UI
 * display.
 *
 * @return the [List] of [AdministrativeUnitViewData] converted
 */
fun Collection<AdministrativeUnit>
        .toAdministrativeUnitViewDataList(): List<AdministrativeUnitViewData> {
    return map { it.toAdministrativeUnitViewData() }
}

/**
 * Converts the [AdministrativeLevel] model into the view object [AdministrativeLevelViewData]
 * used to represent administrative units in the UI.
 *
 * @return the [AdministrativeLevelViewData] converted
 */
fun AdministrativeLevel.toAdministrativeUnitLevelViewData(): AdministrativeLevelViewData {
    return AdministrativeLevelViewData(
        title = name,
        columnCount = administrativeUnitSize,
        zIndex = zIndex
    )
}

/**
 * Converts a collection of [AdministrativeLevel] models into a list of
 * [AdministrativeLevelViewData] view objects used to represent  a list of administrative levels in
 * the UI. Each [AdministrativeLevel] is converted to an [AdministrativeLevelViewData] object
 * using its `toAdministrativeUnitLevelViewData` method, which likely extracts relevant data for UI
 * display.
 *
 * @return the [List] of [AdministrativeLevelViewData] converted
 */
fun List<AdministrativeLevel>
        .toAdministrativeLevelViewDataList(): List<AdministrativeLevelViewData> {
    return map { it.toAdministrativeUnitLevelViewData() }
}

/**
 * Recursively iterates over all sub-administrative units within an [AdministrativeUnit] and creates
 * a list of converted [CartographicBoundaryViewData] objects.
 *
 * @param administrativeUnit the root [AdministrativeUnit] to start the iteration
 * @param cartographicBoundaries an empty or pre-populated list to store the converted data
 * (optional)
 * @return a list containing [CartographicBoundaryViewData] objects for all encountered cartographic
 * boundaries
 */
fun AdministrativeUnit.flatMapSubCartographicBoundariesViewDataList(
    administrativeUnit: AdministrativeUnit,
    cartographicBoundaries: MutableList<CartographicBoundaryViewData> = mutableListOf()
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

