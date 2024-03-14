package io.schiar.fridgnet.view.viewdata

data class AddressLocationImagesViewData(
    val address: String,
    val location: LocationViewData,
    val images: List<ImageViewData>,
    val imagesBoundingBox: BoundingBoxViewData?
)