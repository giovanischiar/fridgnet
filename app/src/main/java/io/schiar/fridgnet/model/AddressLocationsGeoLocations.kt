package io.schiar.fridgnet.model

data class AddressLocationsGeoLocations(
    val address: Address,
    val geoLocations: List<GeoLocation>,
    val administrativeLevelLocation: Map<AdministrativeLevel, Location>
)