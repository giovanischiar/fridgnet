package io.schiar.fridgnet.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.schiar.fridgnet.model.repository.AdministrativeUnitRepository
import io.schiar.fridgnet.view.administrationunit.AdministrativeUnitUiState
import io.schiar.fridgnet.viewmodel.util.toAdministrativeUnitViewData
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

/**
 * The AdministrativeUnitViewModel is the point of connection between the Administrative Unit
 * screen and its Repository
 */
@HiltViewModel
class AdministrativeUnitViewModel @Inject constructor(
    administrativeUnitRepository: AdministrativeUnitRepository
) : ViewModel() {
    /**
     * The stream (Flow) of UI state that contains the Administrative Unit.
     */
    val uiState = administrativeUnitRepository.administrativeUnitFlow
        .map { administrativeUnit ->
            AdministrativeUnitUiState.AdministrativeUnitLoaded(
                administrativeUnit = administrativeUnit.toAdministrativeUnitViewData()
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Lazily,
            initialValue = AdministrativeUnitUiState.Loading
        )
}