package io.schiar.fridgnet.view.viewdata

data class RegionViewData(
    val polygon: PolygonViewData,
    val holes: List<PolygonViewData>,
    val active: Boolean = true,
    val boundingBox: BoundingBoxViewData,
    val center: CoordinateViewData
)