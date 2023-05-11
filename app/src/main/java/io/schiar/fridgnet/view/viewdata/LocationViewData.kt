package io.schiar.fridgnet.view.viewdata

data class LocationViewData(
    val regions: List<RegionViewData>,
    val boundingBox: BoundingBoxViewData
)