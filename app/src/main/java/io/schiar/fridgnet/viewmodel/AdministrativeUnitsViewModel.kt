package io.schiar.fridgnet.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.schiar.fridgnet.Log
import io.schiar.fridgnet.model.repository.AdministrativeUnitsRepository
import io.schiar.fridgnet.viewmodel.util.toAdministrativeLevelViewDataList
import io.schiar.fridgnet.viewmodel.util.toAdministrativeUnitLevelViewData
import io.schiar.fridgnet.viewmodel.util.toAdministrativeUnitViewDataList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AdministrativeUnitsViewModel @Inject constructor(
    private val administrativeUnitsRepository: AdministrativeUnitsRepository
) : ViewModel() {
    val administrativeUnitsFlow = administrativeUnitsRepository.administrativeUnitsFlow
        .map { administrativeUnits -> administrativeUnits.toAdministrativeUnitViewDataList() }
    val administrativeLevelsFlow = administrativeUnitsRepository.administrativeLevelsFlow
        .map { administrativeLevels -> administrativeLevels.toAdministrativeLevelViewDataList() }
    val currentAdministrativeLevelFlow = administrativeUnitsRepository
        .currentAdministrativeLevelFlow
        .map { administrativeLevel -> administrativeLevel.toAdministrativeUnitLevelViewData() }

    fun selectCartographicBoundaryGeoLocationAt(index: Int) = viewModelScope.launch {
        Log.d(
            tag = "Select Image Feature",
            msg ="Select cartographic boundary geo location at $index"
        )
        administrativeUnitsRepository.selectAdministrativeUnitAt(index = index)
    }

    fun changeCurrentAdministrativeLevel(index: Int) = viewModelScope.launch(Dispatchers.IO) {
        administrativeUnitsRepository.changeCurrentAdministrativeLevel(index = index)
    }

    fun removeAllImages() = viewModelScope.launch {
        administrativeUnitsRepository.removeAllImages()
    }
}