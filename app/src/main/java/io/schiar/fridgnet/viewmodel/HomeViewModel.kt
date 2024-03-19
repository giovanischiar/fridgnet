package io.schiar.fridgnet.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.schiar.fridgnet.Log
import io.schiar.fridgnet.model.repository.HomeRepository
import io.schiar.fridgnet.viewmodel.util.toLocationGeoLocationViewDataList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class HomeViewModel(private val homeRepository: HomeRepository) : ViewModel() {
    val locationGeoLocations = homeRepository.locationGeoLocations
        .map { it.toLocationGeoLocationViewDataList()  }
    val administrativeLevels = homeRepository.administrativeLevels.map { administrativeLevels ->
        administrativeLevels.map { it.toString() }
    }
    val currentAdministrativeLevel = homeRepository.currentAdministrativeLevel.map { it.toString() }

    fun selectLocationGeoLocationAt(index: Int) = viewModelScope.launch {
        Log.d("Select Image Feature", "Select location geo location at $index")
        homeRepository.selectLocationGeoLocationAt(index = index)
    }

    fun changeCurrentAdministrativeLevel(index: Int) = viewModelScope.launch(Dispatchers.IO) {
        homeRepository.changeCurrentAdministrativeLevel(index = index)
    }

    fun removeAllImages() = viewModelScope.launch {
        homeRepository.removeAllImages()
    }
}