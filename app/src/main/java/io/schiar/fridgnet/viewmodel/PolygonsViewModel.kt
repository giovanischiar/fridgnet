package io.schiar.fridgnet.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.schiar.fridgnet.model.repository.PolygonsRepository
import io.schiar.fridgnet.view.viewdata.LocationViewData
import io.schiar.fridgnet.view.viewdata.RegionViewData
import io.schiar.fridgnet.viewmodel.util.toLocationViewData
import io.schiar.fridgnet.viewmodel.util.toRegion
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class PolygonsViewModel(private val polygonsRepository: PolygonsRepository) : ViewModel() {
    private val _currentLocation = MutableStateFlow<LocationViewData?>(null)
    val currentLocation: StateFlow<LocationViewData?> = _currentLocation.asStateFlow()

    fun updateCurrentLocation() {
        _currentLocation.update { polygonsRepository.currentLocation()?.toLocationViewData() }
    }

    fun switchRegion(regionViewData: RegionViewData) = viewModelScope.launch {
        polygonsRepository.switchRegion(
            region = regionViewData.toRegion(),
            onCurrentLocationChanged = ::updateCurrentLocation
        )
    }

    fun switchAll() = viewModelScope.launch {
        polygonsRepository.switchAll(onCurrentLocationChanged = ::updateCurrentLocation)
    }
}