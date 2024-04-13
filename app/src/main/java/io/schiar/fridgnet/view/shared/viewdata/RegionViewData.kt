package io.schiar.fridgnet.view.shared.viewdata

/**
 * The data necessary for displaying the Region on the View.
 *
 * @property polygon the outermost Polygon object defining the boundary of the region.
 * @property holes a list of Polygon objects representing interior holes that define areas
 * excluded from the region.
 * @property active true if the region is currently displayed on the map, false otherwise (defaults
 * to true).
 * @property boundingBox the BoundingBox object encompassing the entire region (including holes).
 * @property center the center of the BoundingBox
 * @property zIndex the layer order on the map where the region should be drawn (higher zIndex
 * means drawn on top of others).
 */
data class RegionViewData(
    val polygon: PolygonViewData,
    val holes: List<PolygonViewData>,
    val active: Boolean = true,
    val boundingBox: BoundingBoxViewData,
    val center: GeoLocationViewData,
    val zIndex: Float
)