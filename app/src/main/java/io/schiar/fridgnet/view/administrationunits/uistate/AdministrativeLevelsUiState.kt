package io.schiar.fridgnet.view.administrationunits.uistate

import androidx.compose.runtime.Immutable
import io.schiar.fridgnet.view.administrationunits.uistate.AdministrativeLevelsUiState.AdministrativeLevelsLoaded
import io.schiar.fridgnet.view.administrationunits.uistate.AdministrativeLevelsUiState.Loading
import io.schiar.fridgnet.view.shared.viewdata.AdministrativeLevelViewData

/**
 * Represents the UI state for administrative levels data.
 *
 * This sealed interface encapsulates all possible states that the UI can be in
 * while handling the administrative levels data. It helps in managing the UI state
 * in a type-safe manner and ensures that all possible states are handled explicitly.
 *
 * The possible states are:
 * - [Loading]: Indicates that the administrative levels data is being loaded.
 * - [AdministrativeLevelsLoaded]: Indicates that the administrative levels data has been
 * successfully loaded.
 *
 * Each state holds the necessary data required to represent that state in the UI.
 */
@Immutable
sealed interface AdministrativeLevelsUiState {
    /**
     * Represents the loading state of the administrative units data.
     */
    data object Loading : AdministrativeLevelsUiState

    /**
     * Represents the loaded state of the administrative units data.
     *
     * @property administrativeLevels The loaded administrative levels data.
     */
    data class AdministrativeLevelsLoaded(
        val administrativeLevels: List<AdministrativeLevelViewData>,
    ): AdministrativeLevelsUiState
}