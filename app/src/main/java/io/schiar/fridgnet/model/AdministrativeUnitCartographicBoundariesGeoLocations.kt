package io.schiar.fridgnet.model

data class AdministrativeUnitCartographicBoundariesGeoLocations(
    val administrativeUnit: AdministrativeUnit,
    val geoLocations: List<GeoLocation>,
    val administrativeLevelCartographicBoundary: Map<AdministrativeLevel, CartographicBoundary>
)