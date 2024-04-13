package io.schiar.fridgnet.viewmodel

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import io.schiar.fridgnet.model.repository.AdministrativeUnitRepository
import io.schiar.fridgnet.viewmodel.util.toAdministrativeUnitViewData
import kotlinx.coroutines.flow.map
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
     * The stream (Flow) of Administrative Unit converted into UI object
     */
    val administrativeUnitFlow = administrativeUnitRepository.administrativeUnitFlow.map {
        administrativeUnit -> administrativeUnit.toAdministrativeUnitViewData()
    }
}