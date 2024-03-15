package io.schiar.fridgnet.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.schiar.fridgnet.Log
import io.schiar.fridgnet.model.repository.HomeRepository
import io.schiar.fridgnet.viewmodel.util.toLocationCoordinateViewDataList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class HomeViewModel(private val homeRepository: HomeRepository) : ViewModel() {
    val locationCoordinates = homeRepository.locationCoordinates
        .map { it.toLocationCoordinateViewDataList()  }
    val administrativeUnits = homeRepository.administrativeUnits.map { administrativeUnits ->
        administrativeUnits.map { it.toString() }
    }
    val currentAdministrativeUnit = homeRepository.currentAdministrativeUnit.map { it.toString() }

    fun selectLocationCoordinateAt(index: Int) = viewModelScope.launch {
        Log.d("Select Image Feature", "Select location coordinate at $index")
        homeRepository.selectAddressLocationCoordinateAt(index = index)
    }

    fun changeCurrentAdministrativeUnit(index: Int) = viewModelScope.launch(Dispatchers.IO) {
        homeRepository.changeCurrentAdministrativeUnit(index = index)
    }

    fun removeAllImages() = viewModelScope.launch {
        homeRepository.removeAllImages()
    }
}