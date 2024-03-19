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
    val administrativeUnits = homeRepository.administrativeUnits.map { administrativeUnits ->
        administrativeUnits.map { it.toString() }
    }
    val currentAdministrativeUnit = homeRepository.currentAdministrativeUnit.map { it.toString() }

    fun selectLocationGeoLocationAt(index: Int) = viewModelScope.launch {
        Log.d("Select Image Feature", "Select location geo location at $index")
        homeRepository.selectLocationGeoLocationAt(index = index)
    }

    fun changeCurrentAdministrativeUnit(index: Int) = viewModelScope.launch(Dispatchers.IO) {
        homeRepository.changeCurrentAdministrativeUnit(index = index)
    }

    fun removeAllImages() = viewModelScope.launch {
        homeRepository.removeAllImages()
    }
}