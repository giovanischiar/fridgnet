package io.schiar.fridgnet.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.schiar.fridgnet.Log
import io.schiar.fridgnet.model.repository.HomeRepository
import io.schiar.fridgnet.viewmodel.util.toAdministrativeUnitViewDataList
import io.schiar.fridgnet.viewmodel.util.toStrings
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class HomeViewModel(private val homeRepository: HomeRepository) : ViewModel() {
    val administrativeUnitsFlow = homeRepository.administrativeUnitsFlow
        .map { administrativeUnits -> administrativeUnits.toAdministrativeUnitViewDataList() }
    val administrativeLevelsFlow = homeRepository.administrativeLevelsFlow
        .map { administrativeLevels -> administrativeLevels.toStrings() }
    val currentAdministrativeLevelFlow = homeRepository.currentAdministrativeLevelFlow
        .map { administrativeLevel -> administrativeLevel.toString() }

    fun selectCartographicBoundaryGeoLocationAt(index: Int) = viewModelScope.launch {
        Log.d(
            tag = "Select Image Feature",
            msg ="Select cartographic boundary geo location at $index"
        )
        homeRepository.selectAdministrativeUnitAt(index = index)
    }

    fun changeCurrentAdministrativeLevel(index: Int) = viewModelScope.launch(Dispatchers.IO) {
        homeRepository.changeCurrentAdministrativeLevel(index = index)
    }

    fun removeAllImages() = viewModelScope.launch {
        homeRepository.removeAllImages()
    }
}