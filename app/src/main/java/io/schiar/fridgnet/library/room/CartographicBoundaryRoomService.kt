package io.schiar.fridgnet.library.room

import io.schiar.fridgnet.model.AdministrativeUnit
import io.schiar.fridgnet.model.CartographicBoundary
import io.schiar.fridgnet.model.Region
import io.schiar.fridgnet.model.service.CartographicBoundaryService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class CartographicBoundaryRoomService(
    private val cartographicBoundaryDAO: CartographicBoundaryDAO
) : CartographicBoundaryService {
    override fun retrieve(): Flow<List<CartographicBoundary>> {
        return cartographicBoundaryDAO.selectCartographicBoundariesWithRegions()
            .map {
                cartographicBoundariesWithRegions -> cartographicBoundariesWithRegions.toLocations()
            }
    }

    override fun retrieveRegions(): Flow<List<Region>> {
        return cartographicBoundaryDAO.selectRegions().map { it.toRegions() }
    }

    override fun retrieve(region: Region): Flow<CartographicBoundary?> {
        return cartographicBoundaryDAO.select(regionID = region.id).map {
            it?.toCartographicBoundary()
        }
    }

    override fun retrieve(administrativeUnit: AdministrativeUnit): Flow<CartographicBoundary?> {
        return selectCartographicBoundaryByAdministrativeUnit(
            administrativeUnit = administrativeUnit
        )
    }

    fun selectCartographicBoundaryByAdministrativeUnit(
        administrativeUnit: AdministrativeUnit
    ): Flow<CartographicBoundary?> {
        return cartographicBoundaryDAO.selectCartographicBoundaryWithRegionsByAdministrativeUnit(
            administrativeUnitID = administrativeUnit.id
        ).map { it?.toCartographicBoundary() }
    }

    override suspend fun create(cartographicBoundary: CartographicBoundary) {
        cartographicBoundaryDAO.insert(cartographicBoundary = cartographicBoundary)
    }

    override suspend fun update(cartographicBoundary: CartographicBoundary) {
        cartographicBoundaryDAO.update(cartographicBoundary = cartographicBoundary)
    }
}