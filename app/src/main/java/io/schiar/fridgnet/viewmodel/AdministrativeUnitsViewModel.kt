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

/**
 * The AdministrativeUnitsViewModel is the point of connection between the Administrative Units
 * screen and its Repository
 */
@HiltViewModel
class AdministrativeUnitsViewModel @Inject constructor(
    private val administrativeUnitsRepository: AdministrativeUnitsRepository
) : ViewModel() {
    /**
     * The stream (Flow) of Administrative Units converted into UI objects
     */
    val administrativeUnitsFlow = administrativeUnitsRepository.administrativeUnitsFlow
        .map { administrativeUnits -> administrativeUnits.toAdministrativeUnitViewDataList() }

    /**
     * The stream (Flow) of Administrative Levels converted into UI objects
     */
    val administrativeLevelsFlow = administrativeUnitsRepository.administrativeLevelsFlow
        .map { administrativeLevels -> administrativeLevels.toAdministrativeLevelViewDataList() }

    /**
     * The stream (Flow) of the current Administrative Level converted into UI object
     */
    val currentAdministrativeLevelFlow = administrativeUnitsRepository
        .currentAdministrativeLevelFlow
        .map { administrativeLevel -> administrativeLevel.toAdministrativeUnitLevelViewData() }

    /**
     * Delegates the Repository to update the selected administrative unit in the model based on the
     * provided index. It creates a coroutine to do that.
     *
     * @param index the index of the selected administrative unit
     */
    fun selectCartographicBoundaryGeoLocationAt(index: Int) = viewModelScope.launch {
        Log.d(
            tag = "Select Image Feature",
            msg ="Select cartographic boundary geo location at $index"
        )
        administrativeUnitsRepository.selectAdministrativeUnitAt(index = index)
    }

    /**
     * Delegates the Repository to update the current administrative level to show the new
     * administrative units filtered by the level index provided. It creates a coroutine to do that.
     *
     * @param index the index of the administrative level option provided.
     */
    fun changeCurrentAdministrativeLevel(index: Int) = viewModelScope.launch(Dispatchers.IO) {
        administrativeUnitsRepository.changeCurrentAdministrativeLevel(index = index)
    }

    /**
     * Delegates the repository to remove all images. It creates a coroutine to do that.
     */
    fun removeAllImages() = viewModelScope.launch {
        administrativeUnitsRepository.removeAllImages()
    }
}