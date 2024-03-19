package io.schiar.fridgnet.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.schiar.fridgnet.Log
import io.schiar.fridgnet.model.repository.HomeRepository
import io.schiar.fridgnet.viewmodel.util.toCartographicBoundaryGeoLocationViewDataList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class HomeViewModel(private val homeRepository: HomeRepository) : ViewModel() {
    val cartographicBoundaryGeoLocations = homeRepository.cartographicBoundaryGeoLocations
        .map { it.toCartographicBoundaryGeoLocationViewDataList()  }
    val administrativeLevels = homeRepository.administrativeLevels.map { administrativeLevels ->
        administrativeLevels.map { it.toString() }
    }
    val currentAdministrativeLevel = homeRepository.currentAdministrativeLevel.map { it.toString() }

    fun selectCartographicBoundaryGeoLocationAt(index: Int) = viewModelScope.launch {
        Log.d("Select Image Feature", "Select cartographic boundary geo location at $index")
        homeRepository.selectCartographicBoundaryGeoLocationAt(index = index)
    }

    fun changeCurrentAdministrativeLevel(index: Int) = viewModelScope.launch(Dispatchers.IO) {
        homeRepository.changeCurrentAdministrativeLevel(index = index)
    }

    fun removeAllImages() = viewModelScope.launch {
        homeRepository.removeAllImages()
    }
}