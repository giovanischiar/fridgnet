package io.schiar.fridgnet.model.repository

import io.schiar.fridgnet.Log
import io.schiar.fridgnet.model.CartographicBoundary
import io.schiar.fridgnet.model.datasource.CurrentRegionDataSource
import io.schiar.fridgnet.model.datasource.CartographicBoundaryDataSource
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update

class PolygonsRepository(
    currentRegionDataSource: CurrentRegionDataSource,
    private val cartographicBoundaryDataSource: CartographicBoundaryDataSource
) {
    private var _currentCartographicBoundary: CartographicBoundary? = null
    private val currentCartographicBoundaryFlow = MutableStateFlow(
        value = _currentCartographicBoundary
    )
    @OptIn(ExperimentalCoroutinesApi::class)
    val currentCartographicBoundary = merge(currentRegionDataSource.retrieve()
        .flatMapLatest { region ->
            if (region == null) {
                flowOf(value = null)
            } else cartographicBoundaryDataSource.retrieve(region = region)
                .onEach { cartographicBoundary ->
                    Log.d("", "new cartographic boundary $cartographicBoundary")
                    _currentCartographicBoundary = cartographicBoundary
                }
    }, currentCartographicBoundaryFlow).distinctUntilChanged()

    suspend fun switchRegionAt(index: Int) {
        val currentCartographicBoundary = (_currentCartographicBoundary ?: return)
            .switchRegionAt(index = index)
        Log.d("", "switchRegionAt($index)")
        _currentCartographicBoundary = currentCartographicBoundary
        currentCartographicBoundaryFlow.update { currentCartographicBoundary }
        cartographicBoundaryDataSource.update(cartographicBoundary = currentCartographicBoundary)
    }

    suspend fun switchAll() {
        val currentCartographicBoundary = (_currentCartographicBoundary ?: return).switchAll()
        _currentCartographicBoundary = currentCartographicBoundary
        currentCartographicBoundaryFlow.update { currentCartographicBoundary }
        cartographicBoundaryDataSource.update(cartographicBoundary = currentCartographicBoundary)
    }
}