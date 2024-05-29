package io.schiar.fridgnet.view.regionsandimages.uistate

import androidx.compose.runtime.Immutable
import io.schiar.fridgnet.view.regionsandimages.uistate.VisibleRegionsUiState.Loading
import io.schiar.fridgnet.view.regionsandimages.uistate.VisibleRegionsUiState.VisibleRegionsLoaded
import io.schiar.fridgnet.view.shared.viewdata.RegionViewData

/**
 * Represents the UI state for visible regions data.
 *
 * This sealed interface encapsulates all possible states that the UI can be in
 * while handling the visible regions data. It helps in managing the UI state
 * in a type-safe manner and ensures that all possible states are handled explicitly.
 *
 * The possible states are:
 * - [Loading]: Indicates that the visible regions data are being loaded.
 * - [VisibleRegionsLoaded]: Indicates that the visible regions data have been
 * successfully loaded.
 *
 * Each state holds the necessary data required to represent that state in the UI.
 */
@Immutable
sealed interface VisibleRegionsUiState {
    /**
     * Represents the loading state of the bounding box regions data.
     */
    data object Loading : VisibleRegionsUiState

    /**
     * Represents the loaded state of the visible regions data.
     *
     * @property visibleRegions The loaded visible regions data.
     */
    data class VisibleRegionsLoaded(
        val visibleRegions: List<RegionViewData>,
    ): VisibleRegionsUiState
}