package io.schiar.fridgnet.model

data class AddressCoordinates(
    val address: Address,
    val coordinates: List<Coordinate>
) {
    fun with(coordinate: Coordinate): AddressCoordinates {
        return AddressCoordinates(
            address = address,
            coordinates = coordinates + coordinate
        )
    }
}