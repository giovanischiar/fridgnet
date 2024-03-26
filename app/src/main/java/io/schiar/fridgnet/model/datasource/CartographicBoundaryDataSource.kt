package io.schiar.fridgnet.model.datasource

import io.schiar.fridgnet.model.AdministrativeUnitName
import io.schiar.fridgnet.model.CartographicBoundary
import io.schiar.fridgnet.model.Region
import kotlinx.coroutines.flow.Flow

interface CartographicBoundaryDataSource {
    suspend fun create(cartographicBoundary: CartographicBoundary)
    fun retrieve(): Flow<CartographicBoundary>
    fun retrieveRegions(): Flow<List<Region>>
    fun retrieve(region: Region): Flow<CartographicBoundary?>
    fun retrieve(administrativeUnitName: AdministrativeUnitName): Flow<CartographicBoundary?>
    suspend fun update(cartographicBoundary: CartographicBoundary)
}