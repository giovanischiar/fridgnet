package io.schiar.fridgnet.model.datasource.local

import io.schiar.fridgnet.Log
import io.schiar.fridgnet.model.AdministrativeLevel
import io.schiar.fridgnet.model.AdministrativeUnit
import io.schiar.fridgnet.model.CartographicBoundary
import io.schiar.fridgnet.model.Region
import io.schiar.fridgnet.model.datasource.CartographicBoundaryDataSource
import io.schiar.fridgnet.model.datasource.retriever.CartographicBoundaryRetriever
import io.schiar.fridgnet.model.service.CartographicBoundaryService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import java.util.Collections.synchronizedMap as syncMapOf
import java.util.Collections.synchronizedSet as syncSetOf

class CartographicBoundaryAPIDBDataSource(
    private val cartographicBoundaryRetriever: CartographicBoundaryRetriever,
    private val cartographicBoundaryService: CartographicBoundaryService
): CartographicBoundaryDataSource {
    private val administrativeUnitSet: MutableSet<String> = syncSetOf(mutableSetOf())
    private val administrativeUnitLocationCache
        : MutableMap<AdministrativeUnit, CartographicBoundary> = syncMapOf(mutableMapOf())
    private val cartographicBoundariesCacheFlow
        : MutableStateFlow<List<CartographicBoundary>> = MutableStateFlow(
        value = administrativeUnitLocationCache.values.toList()
    )

    private suspend fun create(cartographicBoundary: CartographicBoundary) {
        cartographicBoundariesCacheFlow.update { administrativeUnitLocationCache.values.toList() }
        cartographicBoundaryService.create(cartographicBoundary = cartographicBoundary)
    }

    private fun updateCache(
        administrativeUnit: AdministrativeUnit, cartographicBoundary: CartographicBoundary
    ) {
        administrativeUnitLocationCache[administrativeUnit] = cartographicBoundary
    }

    private fun updateCache(cartographicBoundaries: List<CartographicBoundary>) {
        cartographicBoundaries.forEach {
            updateCache(administrativeUnit = it.administrativeUnit, cartographicBoundary = it)
        }
    }

    override suspend fun retrieveLocationFor(
        administrativeUnit: AdministrativeUnit, administrativeLevel: AdministrativeLevel
    ) {
        val administrativeUnitAdministrativeLevel = Pair(administrativeUnit, administrativeLevel)
        val administrativeUnitName = administrativeUnit.name(
            administrativeLevel = administrativeLevel
        )
        if (administrativeUnitSet.contains(element = administrativeUnitName)) return
        administrativeUnitSet.add(element = administrativeUnitName)
        log(
            administrativeUnitAdministrativeLevel = administrativeUnitAdministrativeLevel,
            msg = "It's not on memory, retrieving using the API"
        )
        val cartographicBoundaryFromRetriever = when(administrativeLevel) {
            AdministrativeLevel.CITY -> {
                cartographicBoundaryRetriever.retrieveLocality(
                    administrativeUnit = administrativeUnit
                )
            }
            AdministrativeLevel.COUNTY -> {
                cartographicBoundaryRetriever.retrieveSubAdmin(
                    administrativeUnit = administrativeUnit
                )
            }
            AdministrativeLevel.STATE -> {
                cartographicBoundaryRetriever.retrieveAdmin(
                    administrativeUnit = administrativeUnit
                )
            }
            AdministrativeLevel.COUNTRY -> {
                cartographicBoundaryRetriever.retrieveCountry(
                    administrativeUnit = administrativeUnit
                )
            }
        }
        if (cartographicBoundaryFromRetriever != null) {
            create(cartographicBoundary = cartographicBoundaryFromRetriever)
            return
        }
        log(
            administrativeUnitAdministrativeLevel = administrativeUnitAdministrativeLevel,
            msg = "It's not on the API!"
        )
    }

    override fun retrieve(): Flow<List<CartographicBoundary>> {
        return merge(
            cartographicBoundariesCacheFlow,
            cartographicBoundaryService.retrieve().onEach(::updateCache)
        ).distinctUntilChanged()
    }

    override fun retrieve(region: Region): Flow<CartographicBoundary?> {
        return cartographicBoundaryService.retrieve(region = region)
    }

    override fun retrieveRegions(): Flow<List<Region>> {
        return cartographicBoundaryService.retrieveRegions()
    }

    override suspend fun update(cartographicBoundary: CartographicBoundary) {
        cartographicBoundaryService.update(cartographicBoundary = cartographicBoundary)
    }

    private fun log(
        administrativeUnitAdministrativeLevel: Pair<AdministrativeUnit, AdministrativeLevel>,
        msg: String
    ) {
        val (administrativeUnit, administrativeLevel) = administrativeUnitAdministrativeLevel
        val administrativeUnitName = administrativeUnit.name(
            administrativeLevel = administrativeLevel
        )
        Log.d(
            "AdministrativeUnit to Cartographic Boundary Feature",
            "Retrieving Cartographic Boundary for $administrativeUnitName: $msg"
        )
    }
}