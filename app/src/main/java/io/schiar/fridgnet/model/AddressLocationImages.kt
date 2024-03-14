package io.schiar.fridgnet.model

data class AddressLocationImages(
    val address: Address, val location: Location, val images: List<Image>
)