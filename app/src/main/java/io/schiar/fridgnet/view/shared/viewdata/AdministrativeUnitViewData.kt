package io.schiar.fridgnet.view.shared.viewdata

data class AdministrativeUnitViewData(
    val name: String,
    val administrativeLevel: AdministrativeLevelViewData,
    val cartographicBoundary: CartographicBoundaryViewData?,
    val subCartographicBoundaries: List<CartographicBoundaryViewData>,
    val images: List<ImageViewData>,
    val imagesBoundingBox: BoundingBoxViewData?
)