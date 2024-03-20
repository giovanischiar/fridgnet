package io.schiar.fridgnet.view.viewdata

data class AdministrativeUnitViewData(
    val name: String,
    val administrativeLevel: String,
    val cartographicBoundary: CartographicBoundaryViewData?,
    val subAdministrativeUnitNames: List<AdministrativeUnitViewData>,
    val images: List<ImageViewData>,
    val imagesBoundingBox: BoundingBoxViewData?
)