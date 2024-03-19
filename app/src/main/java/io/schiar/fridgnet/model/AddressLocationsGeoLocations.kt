package io.schiar.fridgnet.model

data class AddressLocationsGeoLocations(
    val address: Address,
    val geoLocations: List<GeoLocation>,
    val administrativeUnitLocation: Map<AdministrativeUnit, Location>
)