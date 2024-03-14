package io.schiar.fridgnet.model.repository

import io.schiar.fridgnet.Log
import io.schiar.fridgnet.model.Location
import io.schiar.fridgnet.model.datasource.CurrentRegionDataSource
import io.schiar.fridgnet.model.datasource.LocationDataSource
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
    private val locationDataSource: LocationDataSource
) {
    private var _currentLocation: Location? = null
    private val currentLocationFlow = MutableStateFlow(value = _currentLocation)
    @OptIn(ExperimentalCoroutinesApi::class)
    val currentLocation = merge(currentRegionDataSource.retrieve().flatMapLatest { region ->
        if (region == null) flowOf(value = null) else locationDataSource.retrieve(
            region = region
        ).onEach {
            Log.d("", "new location $it")
            _currentLocation = it
        }
    }, currentLocationFlow).distinctUntilChanged()

    suspend fun switchRegionAt(index: Int) {
        val currentLocation = (_currentLocation ?: return).switchRegionAt(index = index)
        Log.d("", "switchRegionAt($index)")
        _currentLocation = currentLocation
        currentLocationFlow.update { currentLocation }
        locationDataSource.update(location = currentLocation)
    }

    suspend fun switchAll() {
        val currentLocation = (_currentLocation ?: return).switchAll()
        _currentLocation = currentLocation
        currentLocationFlow.update { currentLocation }
        locationDataSource.update(location = currentLocation)
    }
}