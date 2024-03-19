package io.schiar.fridgnet.model.datasource

import io.schiar.fridgnet.model.Address
import io.schiar.fridgnet.model.AdministrativeUnit
import io.schiar.fridgnet.model.Location
import io.schiar.fridgnet.model.Region
import kotlinx.coroutines.flow.Flow

interface LocationDataSource {
    suspend fun retrieveLocationFor(address: Address, administrativeUnit: AdministrativeUnit)
    fun retrieve(): Flow<List<Location>>
    fun retrieveRegions(): Flow<List<Region>>
    fun retrieve(region: Region): Flow<Location?>
    suspend fun update(location: Location)
}