package io.schiar.fridgnet.library.room

import io.schiar.fridgnet.model.AdministrativeUnitName
import io.schiar.fridgnet.model.CartographicBoundary
import io.schiar.fridgnet.model.Region
import io.schiar.fridgnet.model.datasource.CartographicBoundaryDataSource
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

class CartographicBoundaryRoomDataSource(
    private val cartographicBoundaryDAO: CartographicBoundaryDAO
) : CartographicBoundaryDataSource {
    private val cartographicBoundariesSet = mutableSetOf<CartographicBoundary>()

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun retrieve(): Flow<CartographicBoundary> {
        return cartographicBoundaryDAO.selectCartographicBoundariesWithRegions()
            .flatMapLatest { cartographicBoundaryWithRegionsList ->
                flow {
                    for (cartographicBoundaryWithRegions in cartographicBoundaryWithRegionsList) {
                        val cartographicBoundary = cartographicBoundaryWithRegions
                            .toCartographicBoundary()
                        if (cartographicBoundariesSet.add(element = cartographicBoundary)) {
                            emit(cartographicBoundary)
                        }
                    }
                }
            }
    }

    override fun retrieveRegions(): Flow<List<Region>> {
        return cartographicBoundaryDAO.selectRegions().map { it.toRegions() }
    }

    override fun retrieve(region: Region): Flow<CartographicBoundary?> {
        return cartographicBoundaryDAO.select(regionID = region.id)
            .map { cartographicBoundaryWithRegions ->
                cartographicBoundaryWithRegions?.toCartographicBoundary()
            }
    }

    override fun retrieve(
        administrativeUnitName: AdministrativeUnitName
    ): Flow<CartographicBoundary?> {
        return selectCartographicBoundaryByAdministrativeUnitName(
            administrativeUnitName = administrativeUnitName
        )
    }

    fun selectCartographicBoundaryByAdministrativeUnitName(
        administrativeUnitName: AdministrativeUnitName
    ): Flow<CartographicBoundary?> {
        return cartographicBoundaryDAO
            .selectCartographicBoundaryWithRegionsByAdministrativeUnitName(
                administrativeUnitNameID = administrativeUnitName.id
            )
            .map { it?.toCartographicBoundary() }
    }

    override suspend fun create(cartographicBoundary: CartographicBoundary) {
        cartographicBoundaryDAO.insert(cartographicBoundary = cartographicBoundary)
    }

    override suspend fun update(cartographicBoundary: CartographicBoundary) {
        cartographicBoundaryDAO.update(cartographicBoundary = cartographicBoundary)
    }
}