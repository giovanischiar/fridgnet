package io.schiar.fridgnet.model

data class AddressLocationsCoordinates(
    val address: Address,
    val coordinates: List<Coordinate>,
    val administrativeUnitLocation: Map<AdministrativeUnit, Location>
)