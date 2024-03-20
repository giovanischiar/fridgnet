package io.schiar.fridgnet.view.viewdata

data class AdminUnitViewData(
    val name: String,
    val administrativeLevel: String,
    val cartographicBoundary: CartographicBoundaryViewData?,
    val subAdministrativeUnitNames: List<AdminUnitViewData>,
    val images: List<ImageViewData>,
    val imagesBoundingBox: BoundingBoxViewData?
)