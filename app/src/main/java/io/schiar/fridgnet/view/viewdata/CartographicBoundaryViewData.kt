package io.schiar.fridgnet.view.viewdata

data class CartographicBoundaryViewData(
    val administrativeUnitName: String,
    val regions: List<RegionViewData>,
    val boundingBox: BoundingBoxViewData,
    val center: GeoLocationViewData,
    val zIndex: Float
)