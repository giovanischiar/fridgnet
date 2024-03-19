package io.schiar.fridgnet.view.viewdata

data class CartographicBoundaryImagesViewData(
    val cartographicBoundary: CartographicBoundaryViewData,
    val images: List<ImageViewData>,
    val imagesBoundingBox: BoundingBoxViewData?
)