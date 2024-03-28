package io.schiar.fridgnet.view.viewdata

data class AdministrativeUnitViewData(
    val name: String,
    val administrativeLevel: String,
    val cartographicBoundary: CartographicBoundaryViewData?,
    val subCartographicBoundaries: List<CartographicBoundaryViewData>,
    val images: List<ImageViewData>,
    val imagesBoundingBox: BoundingBoxViewData?
)