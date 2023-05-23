package io.schiar.fridgnet.view.viewdata

data class LocationViewData(
    val address: String,
    val regions: List<RegionViewData>,
    val boundingBox: BoundingBoxViewData,
    val center: CoordinateViewData
)