package io.schiar.fridgnet.viewmodel

import androidx.lifecycle.ViewModel
import io.schiar.fridgnet.model.repository.PolygonsRepository
import io.schiar.fridgnet.view.viewdata.LocationViewData
import io.schiar.fridgnet.view.viewdata.RegionViewData
import io.schiar.fridgnet.viewmodel.util.toLocationViewData
import io.schiar.fridgnet.viewmodel.util.toRegion
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class PolygonsViewModel(private val polygonsRepository: PolygonsRepository) : ViewModel() {
    private val _currentLocation = MutableStateFlow<LocationViewData?>(null)
    val currentLocation: StateFlow<LocationViewData?> = _currentLocation.asStateFlow()

    fun updateCurrentLocation() {
        _currentLocation.update { polygonsRepository.currentLocation()?.toLocationViewData() }
    }

    suspend fun switchRegion(regionViewData: RegionViewData) {
        polygonsRepository.switchRegion(
            region = regionViewData.toRegion(),
            onCurrentLocationChanged = ::updateCurrentLocation
        )
    }

    suspend fun switchAll() {
        polygonsRepository.switchAll(onCurrentLocationChanged = ::updateCurrentLocation)
    }
}