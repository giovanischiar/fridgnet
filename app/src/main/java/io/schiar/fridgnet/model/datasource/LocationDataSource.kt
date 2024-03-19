package io.schiar.fridgnet.model.datasource

import io.schiar.fridgnet.model.AdministrativeUnit
import io.schiar.fridgnet.model.AdministrativeLevel
import io.schiar.fridgnet.model.Location
import io.schiar.fridgnet.model.Region
import kotlinx.coroutines.flow.Flow

interface LocationDataSource {
    suspend fun retrieveLocationFor(administrativeUnit: AdministrativeUnit, administrativeLevel: AdministrativeLevel)
    fun retrieve(): Flow<List<Location>>
    fun retrieveRegions(): Flow<List<Region>>
    fun retrieve(region: Region): Flow<Location?>
    suspend fun update(location: Location)
}