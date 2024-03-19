package io.schiar.fridgnet.view.viewdata

data class LocationViewData(
    val administrativeUnit: String,
    val regions: List<RegionViewData>,
    val boundingBox: BoundingBoxViewData,
    val center: GeoLocationViewData,
    val zIndex: Float
)