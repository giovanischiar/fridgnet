package io.schiar.fridgnet.view.viewdata

data class AddressLocationImagesViewData(
    val addressName: String,
    val location: LocationViewData?,
    val initialCoordinate: CoordinateViewData
)