package io.schiar.fridgnet.model

data class AddressLocationImages(
    val address: Address,
    val location: Location? = null,
    val initialCoordinate: Coordinate
)