package io.schiar.fridgnet.model

data class AdministrativeUnitLocationsGeoLocations(
    val administrativeUnit: AdministrativeUnit,
    val geoLocations: List<GeoLocation>,
    val administrativeLevelLocation: Map<AdministrativeLevel, Location>
)