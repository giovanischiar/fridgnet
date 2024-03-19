package io.schiar.fridgnet.model.datasource

import io.schiar.fridgnet.model.AdministrativeLevel
import io.schiar.fridgnet.model.AdministrativeUnit
import io.schiar.fridgnet.model.CartographicBoundary
import io.schiar.fridgnet.model.Region
import kotlinx.coroutines.flow.Flow

interface CartographicBoundaryDataSource {
    suspend fun retrieveLocationFor(
        administrativeUnit: AdministrativeUnit, administrativeLevel: AdministrativeLevel
    )
    fun retrieve(): Flow<List<CartographicBoundary>>
    fun retrieveRegions(): Flow<List<Region>>
    fun retrieve(region: Region): Flow<CartographicBoundary?>
    suspend fun update(cartographicBoundary: CartographicBoundary)
}