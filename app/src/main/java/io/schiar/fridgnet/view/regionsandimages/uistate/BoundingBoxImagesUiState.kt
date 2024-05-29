package io.schiar.fridgnet.view.regionsandimages.uistate

import androidx.compose.runtime.Immutable
import io.schiar.fridgnet.view.regionsandimages.uistate.BoundingBoxImagesUiState.BoundingBoxImagesLoaded
import io.schiar.fridgnet.view.regionsandimages.uistate.BoundingBoxImagesUiState.Loading
import io.schiar.fridgnet.view.shared.viewdata.BoundingBoxViewData

/**
 * Represents the UI state for bounding box images.
 *
 * This sealed interface encapsulates all possible states that the UI can be in
 * while handling the bounding box data. It helps in managing the UI state
 * in a type-safe manner and ensures that all possible states are handled explicitly.
 *
 * The possible states are:
 * - [Loading]: Indicates that the bounding box images are being loaded.
 * - [BoundingBoxImagesLoaded]: Indicates that the bounding box images have been
 * successfully loaded.
 *
 * Each state holds the necessary data required to represent that state in the UI.
 */
@Immutable
sealed interface BoundingBoxImagesUiState {
    /**
     * Represents the loading state of the bounding box images.
     */
    data object Loading : BoundingBoxImagesUiState

    /**
     * Represents the loaded state of the bounding box images.
     *
     * @property boundingBoxImages The loaded bounding box images.
     */
    data class BoundingBoxImagesLoaded(
        val boundingBoxImages: BoundingBoxViewData,
    ): BoundingBoxImagesUiState
}