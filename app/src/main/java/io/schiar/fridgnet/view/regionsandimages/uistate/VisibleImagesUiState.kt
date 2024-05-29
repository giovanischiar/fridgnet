package io.schiar.fridgnet.view.regionsandimages.uistate

import androidx.compose.runtime.Immutable
import io.schiar.fridgnet.view.regionsandimages.uistate.VisibleImagesUiState.Loading
import io.schiar.fridgnet.view.regionsandimages.uistate.VisibleImagesUiState.VisibleImagesLoaded
import io.schiar.fridgnet.view.shared.viewdata.ImageViewData

/**
 * Represents the UI state for visible images data.
 *
 * This sealed interface encapsulates all possible states that the UI can be in
 * while handling the visible images data. It helps in managing the UI state
 * in a type-safe manner and ensures that all possible states are handled explicitly.
 *
 * The possible states are:
 * - [Loading]: Indicates that the visible images data are being loaded.
 * - [VisibleImagesLoaded]: Indicates that the visible images data have been
 * successfully loaded.
 *
 * Each state holds the necessary data required to represent that state in the UI.
 */
@Immutable
sealed interface VisibleImagesUiState {
    /**
     * Represents the loading state of the visible images data.
     */
    data object Loading : VisibleImagesUiState

    /**
     * Represents the loaded state of the visible images data.
     *
     * @property visibleImages The loaded visible images data.
     */
    data class VisibleImagesLoaded(
        val visibleImages: List<ImageViewData>,
    ): VisibleImagesUiState
}