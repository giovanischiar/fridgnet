package io.schiar.fridgnet.model.datasource

import io.schiar.fridgnet.model.AdministrativeUnit
import io.schiar.fridgnet.model.AdministrativeUnitLocationsGeoLocations
import io.schiar.fridgnet.model.AdministrativeLevel
import io.schiar.fridgnet.model.GeoLocation
import kotlinx.coroutines.flow.Flow

interface AdministrativeUnitDataSource {
    suspend fun create(geoLocation: GeoLocation, administrativeUnit: AdministrativeUnit)
    suspend fun retrieveAdministrativeUnitFor(geoLocation: GeoLocation)
    fun retrieve(): Flow<List<AdministrativeUnitLocationsGeoLocations>>
    fun retrieveGeoLocations(
        administrativeUnit: AdministrativeUnit, administrativeLevel: AdministrativeLevel
    ): Flow<List<GeoLocation>>
}