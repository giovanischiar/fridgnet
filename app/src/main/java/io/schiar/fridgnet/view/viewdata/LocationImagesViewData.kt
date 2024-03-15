package io.schiar.fridgnet.view.viewdata

data class LocationImagesViewData(
    val location: LocationViewData,
    val images: List<ImageViewData>,
    val imagesBoundingBox: BoundingBoxViewData?
)