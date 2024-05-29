package io.schiar.fridgnet.view.administrationunits.uistate

import androidx.compose.runtime.Immutable
import io.schiar.fridgnet.view.administrationunits.uistate.AdministrativeUnitsUiState.AdministrativeUnitsLoaded
import io.schiar.fridgnet.view.administrationunits.uistate.AdministrativeUnitsUiState.Loading
import io.schiar.fridgnet.view.shared.viewdata.AdministrativeUnitViewData

/**
 * Represents the UI state for administrative units data.
 *
 * This sealed interface encapsulates all possible states that the UI can be in
 * while handling the administrative unit data. It helps in managing the UI state
 * in a type-safe manner and ensures that all possible states are handled explicitly.
 *
 * The possible states are:
 * - [Loading]: Indicates that the administrative unit data is being loaded.
 * - [AdministrativeUnitsLoaded]: Indicates that the administrative unit data has been successfully
 * loaded.
 *
 * Each state holds the necessary data required to represent that state in the UI.
 */
@Immutable
sealed interface AdministrativeUnitsUiState {
    /**
     * Represents the loading state of the administrative units data.
     */
    data object Loading : AdministrativeUnitsUiState

    /**
     * Represents the loaded state of the administrative units data.
     *
     * @property administrativeUnits The loaded administrative units data.
     */
    data class AdministrativeUnitsLoaded(
        val administrativeUnits: List<AdministrativeUnitViewData>,
    ): AdministrativeUnitsUiState
}