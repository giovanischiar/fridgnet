package io.schiar.fridgnet.model.datasource.local

import io.schiar.fridgnet.Log
import io.schiar.fridgnet.model.AdministrativeLevel
import io.schiar.fridgnet.model.AdministrativeUnitName
import io.schiar.fridgnet.model.CartographicBoundary
import io.schiar.fridgnet.model.Region
import io.schiar.fridgnet.model.datasource.CartographicBoundaryDataSource
import io.schiar.fridgnet.model.datasource.retriever.CartographicBoundaryRetriever
import io.schiar.fridgnet.model.service.CartographicBoundaryService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.update
import java.util.Collections.synchronizedMap as syncMapOf

class CartographicBoundaryAPIDBDataSource(
    private val cartographicBoundaryRetriever: CartographicBoundaryRetriever,
    private val cartographicBoundaryService: CartographicBoundaryService
): CartographicBoundaryDataSource {
    private val administrativeUnitNameLocationCache
        : MutableMap<AdministrativeUnitName, CartographicBoundary> = syncMapOf(mutableMapOf())
    private val cartographicBoundariesCacheFlow
        : MutableStateFlow<List<CartographicBoundary>> = MutableStateFlow(
        value = administrativeUnitNameLocationCache.values.toList()
    )

    private suspend fun create(cartographicBoundary: CartographicBoundary) {
        cartographicBoundariesCacheFlow.update {
            administrativeUnitNameLocationCache.values.toList()
        }
        cartographicBoundaryService.create(cartographicBoundary = cartographicBoundary)
    }

    override suspend fun retrieveLocationFor(
        administrativeUnitName: AdministrativeUnitName, administrativeLevel: AdministrativeLevel
    ) {
        val administrativeUnitNameAdministrativeLevel = Pair(
            administrativeUnitName, administrativeLevel
        )
        log(
            administrativeUnitNameAdministrativeLevel = administrativeUnitNameAdministrativeLevel,
            msg = "It's not on memory, retrieving using the API"
        )
        val cartographicBoundaryFromRetriever = when(administrativeLevel) {
            AdministrativeLevel.CITY -> {
                cartographicBoundaryRetriever.retrieveLocality(
                    administrativeUnitName = administrativeUnitName
                )
            }
            AdministrativeLevel.COUNTY -> {
                cartographicBoundaryRetriever.retrieveSubAdmin(
                    administrativeUnitName = administrativeUnitName
                )
            }
            AdministrativeLevel.STATE -> {
                cartographicBoundaryRetriever.retrieveAdmin(
                    administrativeUnitName = administrativeUnitName
                )
            }
            AdministrativeLevel.COUNTRY -> {
                cartographicBoundaryRetriever.retrieveCountry(
                    administrativeUnitName = administrativeUnitName
                )
            }
        }
        if (cartographicBoundaryFromRetriever != null) {
            create(cartographicBoundary = cartographicBoundaryFromRetriever)
            return
        }
        log(
            administrativeUnitNameAdministrativeLevel = administrativeUnitNameAdministrativeLevel,
            msg = "It's not on the API!"
        )
    }

    override fun retrieve(): Flow<List<CartographicBoundary>> {
        return merge(
            cartographicBoundariesCacheFlow,
            cartographicBoundaryService.retrieve()
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
        administrativeUnitNameAdministrativeLevel: Pair<
            AdministrativeUnitName, AdministrativeLevel
        >,
        msg: String
    ) {
        val (
            administrativeUnitName, administrativeLevel
        ) = administrativeUnitNameAdministrativeLevel
        val administrativeUnitNameString = administrativeUnitName.toString(
            administrativeLevel = administrativeLevel
        )
        Log.d(
            "AdministrativeUnitName to Cartographic Boundary Feature",
            "Retrieving Cartographic Boundary for $administrativeUnitNameString: $msg"
        )
    }
}