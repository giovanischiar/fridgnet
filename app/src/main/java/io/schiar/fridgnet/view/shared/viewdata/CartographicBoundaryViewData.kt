package io.schiar.fridgnet.view.shared.viewdata

/**
 * The data necessary for displaying a cartographic boundary on the view.
 *
 * @property administrativeUnitName the name of the Administrative Unit that this boundary belongs to.
 * @property regions a list of [RegionViewData] objects. Each object defines a single polygon that
 * contributes to the overall outline of the boundary. Some administrative units may have
 * disconnected territories, hence the list can contain multiple regions.
 * @property boundingBox the bounding box that encloses the entire CartographicBoundary.
 * @property center the geographic location representing the center of the CartographicBoundary.
 * @property zIndex the layer order on the map where the boundary should be drawn (higher zIndex
 * means drawn on top of others).
 */
data class CartographicBoundaryViewData(
    val administrativeUnitName: String,
    val regions: List<RegionViewData>,
    val boundingBox: BoundingBoxViewData,
    val center: GeoLocationViewData,
    val zIndex: Float
)