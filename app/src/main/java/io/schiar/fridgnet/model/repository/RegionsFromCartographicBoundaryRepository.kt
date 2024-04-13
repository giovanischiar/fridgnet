package io.schiar.fridgnet.model.repository

import io.schiar.fridgnet.Log
import io.schiar.fridgnet.model.CartographicBoundary
import io.schiar.fridgnet.model.datasource.CartographicBoundaryDataSource
import io.schiar.fridgnet.model.datasource.CurrentRegionDataSource
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import javax.inject.Inject

/**
 * The repository that exposes and manipulates regions from cartographic boundary screen's flows.
 */
class RegionsFromCartographicBoundaryRepository @Inject constructor(
    currentRegionDataSource: CurrentRegionDataSource,
    private val cartographicBoundaryDataSource: CartographicBoundaryDataSource
) {
    private var currentCartographicBoundary: CartographicBoundary? = null
    private val currentCartographicBoundaryStateFlow = MutableStateFlow(
        value = currentCartographicBoundary
    )

    /**
     * Using the current region, the flow searches for the cartographic boundary that this region
     * belongs to.
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    val currentCartographicBoundaryFlow = merge(
        currentRegionDataSource.retrieve()
            .flatMapLatest { region ->
                if (region == null) return@flatMapLatest flowOf(value = null)
                cartographicBoundaryDataSource.retrieve(region)
                    .onEach { cartographicBoundary ->
                        Log.d("", "new cartographic boundary $cartographicBoundary")
                        currentCartographicBoundary = cartographicBoundary
                    }
            },
        currentCartographicBoundaryStateFlow
    ).filterNotNull().distinctUntilChanged()

    /**
     * Switch the region in a specific index to visible or invisible.
     *
     * @param index the index of the region
     */
    suspend fun switchRegionAt(index: Int) {
        val cartographicBoundary = currentCartographicBoundary ?: return
        val cartographicBoundaryWithRegionSwitched = cartographicBoundary.regionSwitchedAt(index)
        Log.d("", "switchRegionAt($index)")
        currentCartographicBoundary = cartographicBoundaryWithRegionSwitched
        currentCartographicBoundaryStateFlow.update { cartographicBoundaryWithRegionSwitched }
        cartographicBoundaryDataSource.update(
            cartographicBoundary = cartographicBoundaryWithRegionSwitched
        )
    }

    /**
     * Switch all the possible regions (excluding the main region) in a specific index to visible or
     * invisible.
     */
    suspend fun switchAll() {
        val cartographicBoundary = currentCartographicBoundary ?: return
        val cartographicBoundaryWithAllRegionsSwitched = cartographicBoundary.allRegionsSwitched()
        currentCartographicBoundary = cartographicBoundaryWithAllRegionsSwitched
        currentCartographicBoundaryStateFlow.update { cartographicBoundaryWithAllRegionsSwitched }
        cartographicBoundaryDataSource.update(
            cartographicBoundary = cartographicBoundaryWithAllRegionsSwitched
        )
    }
}