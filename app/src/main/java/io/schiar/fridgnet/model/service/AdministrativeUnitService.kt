package io.schiar.fridgnet.model.service

import io.schiar.fridgnet.model.AdministrativeLevel
import io.schiar.fridgnet.model.AdministrativeUnit
import io.schiar.fridgnet.model.CartographicBoundary
import io.schiar.fridgnet.model.GeoLocation
import kotlinx.coroutines.flow.Flow

interface AdministrativeUnitService {
    suspend fun create(geoLocation: GeoLocation, administrativeUnit: AdministrativeUnit)
    fun retrieve(): Flow<List<Pair<AdministrativeUnit, List<CartographicBoundary>>>>
    fun retrieveGeoLocations(
        administrativeUnit: AdministrativeUnit, administrativeLevel: AdministrativeLevel
    ): Flow<List<GeoLocation>>
}