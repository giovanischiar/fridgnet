package io.schiar.fridgnet.model.service

import io.schiar.fridgnet.model.AdministrativeLevel
import io.schiar.fridgnet.model.AdministrativeUnitName
import io.schiar.fridgnet.model.CartographicBoundary
import io.schiar.fridgnet.model.GeoLocation
import kotlinx.coroutines.flow.Flow

interface AdministrativeUnitNameService {
    suspend fun create(geoLocation: GeoLocation, administrativeUnitName: AdministrativeUnitName)
    fun retrieve(): Flow<List<Pair<AdministrativeUnitName, List<CartographicBoundary>>>>
    fun retrieveGeoLocations(
        administrativeUnitName: AdministrativeUnitName, administrativeLevel: AdministrativeLevel
    ): Flow<List<GeoLocation>>
}