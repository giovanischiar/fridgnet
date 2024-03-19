package io.schiar.fridgnet.model.service

import io.schiar.fridgnet.model.AdministrativeUnit
import io.schiar.fridgnet.model.CartographicBoundary
import io.schiar.fridgnet.model.Region
import kotlinx.coroutines.flow.Flow

interface CartographicBoundaryService {
    suspend fun create(cartographicBoundary: CartographicBoundary)
    fun retrieve(): Flow<List<CartographicBoundary>>
    fun retrieveRegions(): Flow<List<Region>>
    fun retrieve(region: Region): Flow<CartographicBoundary?>
    fun retrieve(administrativeUnit: AdministrativeUnit): Flow<CartographicBoundary?>
    suspend fun update(cartographicBoundary: CartographicBoundary)
}