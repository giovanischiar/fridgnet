package io.schiar.fridgnet.model.datasource

import io.schiar.fridgnet.model.AdministrativeLevel
import io.schiar.fridgnet.model.AdministrativeUnitName
import io.schiar.fridgnet.model.CartographicBoundary
import io.schiar.fridgnet.model.Region
import kotlinx.coroutines.flow.Flow

interface CartographicBoundaryDataSource {
    suspend fun retrieveLocationFor(
        administrativeUnitName: AdministrativeUnitName, administrativeLevel: AdministrativeLevel
    )
    fun retrieve(): Flow<List<CartographicBoundary>>
    fun retrieveRegions(): Flow<List<Region>>
    fun retrieve(region: Region): Flow<CartographicBoundary?>
    suspend fun update(cartographicBoundary: CartographicBoundary)
}