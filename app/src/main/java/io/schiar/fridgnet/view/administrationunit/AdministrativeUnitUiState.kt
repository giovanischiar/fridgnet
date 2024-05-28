package io.schiar.fridgnet.view.administrationunit

import androidx.compose.runtime.Immutable
import io.schiar.fridgnet.view.administrationunit.AdministrativeUnitUiState.AdministrativeUnitLoaded
import io.schiar.fridgnet.view.administrationunit.AdministrativeUnitUiState.Loading
import io.schiar.fridgnet.view.shared.viewdata.AdministrativeUnitViewData

/**
 * Represents the UI state for administrative unit data.
 *
 * This sealed interface encapsulates all possible states that the UI can be in
 * while handling the administrative unit data. It helps in managing the UI state
 * in a type-safe manner and ensures that all possible states are handled explicitly.
 *
 * The possible states are:
 * - [Loading]: Indicates that the administrative unit data is being loaded.
 * - [AdministrativeUnitLoaded]: Indicates that the administrative unit data has been successfully
 * loaded.
 *
 * Each state holds the necessary data required to represent that state in the UI.
 */
@Immutable
sealed interface AdministrativeUnitUiState {
    /**
     * Represents the loading state of the administrative unit data.
     */
    data object Loading : AdministrativeUnitUiState

    /**
     * Represents the loaded state of the administrative unit data.
     *
     * @property administrativeUnit The loaded administrative unit data.
     */
    data class AdministrativeUnitLoaded(
        val administrativeUnit: AdministrativeUnitViewData,
    ): AdministrativeUnitUiState
}