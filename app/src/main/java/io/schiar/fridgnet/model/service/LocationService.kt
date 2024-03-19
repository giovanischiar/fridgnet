package io.schiar.fridgnet.model.service

import io.schiar.fridgnet.model.AdministrativeUnit
import io.schiar.fridgnet.model.Location
import io.schiar.fridgnet.model.Region
import kotlinx.coroutines.flow.Flow

interface LocationService {
    suspend fun create(location: Location)
    fun retrieve(): Flow<List<Location>>
    fun retrieveRegions(): Flow<List<Region>>
    fun retrieve(region: Region): Flow<Location?>
    fun retrieve(administrativeUnit: AdministrativeUnit): Flow<Location?>
    suspend fun update(location: Location)
}