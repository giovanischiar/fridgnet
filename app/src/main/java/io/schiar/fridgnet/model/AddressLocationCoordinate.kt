package io.schiar.fridgnet.model

data class AddressLocationCoordinate(
    val address: Address? = null,
    val location: Location? = null,
    val initialCoordinate: Coordinate? = null
) {
    fun with(location: Location): AddressLocationCoordinate {
        return AddressLocationCoordinate(
            address = address,
            location = location,
            initialCoordinate = initialCoordinate
        )
    }

    fun with(address: Address, initialCoordinate: Coordinate): AddressLocationCoordinate {
        return AddressLocationCoordinate(
            address = address,
            location = location,
            initialCoordinate = initialCoordinate
        )
    }
}