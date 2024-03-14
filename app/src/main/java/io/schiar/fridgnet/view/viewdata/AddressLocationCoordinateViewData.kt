package io.schiar.fridgnet.view.viewdata

data class AddressLocationCoordinateViewData(
    val addressName: String?,
    val location: LocationViewData?,
    val initialCoordinate: CoordinateViewData?
)