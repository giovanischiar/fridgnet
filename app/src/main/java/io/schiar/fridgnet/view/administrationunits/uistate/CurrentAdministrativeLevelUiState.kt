package io.schiar.fridgnet.view.administrationunits.uistate

import androidx.compose.runtime.Immutable
import io.schiar.fridgnet.view.administrationunits.uistate.CurrentAdministrativeLevelUiState.CurrentAdministrativeLevelLoaded
import io.schiar.fridgnet.view.administrationunits.uistate.CurrentAdministrativeLevelUiState.Loading
import io.schiar.fridgnet.view.shared.viewdata.AdministrativeLevelViewData

/**
 * Represents the UI state for current administrative level data.
 *
 * This sealed interface encapsulates all possible states that the UI can be in
 * while handling the current administrative level data. It helps in managing the UI state
 * in a type-safe manner and ensures that all possible states are handled explicitly.
 *
 * The possible states are:
 * - [Loading]: Indicates that the current administrative level unit data is being loaded.
 * - [CurrentAdministrativeLevelLoaded]: Indicates that the current administrative level data has
 * been successfully loaded.
 *
 * Each state holds the necessary data required to represent that state in the UI.
 */
@Immutable
sealed interface CurrentAdministrativeLevelUiState {
    /**
     * Represents the loading state of the current administrative level data.
     */
    data object Loading : CurrentAdministrativeLevelUiState

    /**
     * Represents the loaded state of the current administrative level data.
     *
     * @property currentAdministrativeLevel the loaded current administrative level data.
     */
    data class CurrentAdministrativeLevelLoaded(
        val currentAdministrativeLevel: AdministrativeLevelViewData
    ): CurrentAdministrativeLevelUiState
}