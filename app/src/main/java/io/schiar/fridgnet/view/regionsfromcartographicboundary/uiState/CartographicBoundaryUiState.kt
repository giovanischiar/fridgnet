package io.schiar.fridgnet.view.regionsfromcartographicboundary.uiState

import androidx.compose.runtime.Immutable
import io.schiar.fridgnet.view.regionsfromcartographicboundary.uiState.CartographicBoundaryUiState.CartographicBoundaryLoaded
import io.schiar.fridgnet.view.regionsfromcartographicboundary.uiState.CartographicBoundaryUiState.Loading
import io.schiar.fridgnet.view.shared.viewdata.CartographicBoundaryViewData

/**
 * Represents the UI state for cartographic boundary.
 *
 * This sealed interface encapsulates all possible states that the UI can be in
 * while handling the cartographic boundary. It helps in managing the UI state
 * in a type-safe manner and ensures that all possible states are handled explicitly.
 *
 * The possible states are:
 * - [Loading]: Indicates that the cartographic boundary are being loaded.
 * - [CartographicBoundaryLoaded]: Indicates that the cartographic boundary have been
 * successfully loaded.
 *
 * Each state holds the necessary data required to represent that state in the UI.
 */
@Immutable
sealed interface CartographicBoundaryUiState {
    /**
     * Represents the loading state of the bounding box images.
     */
    data object Loading : CartographicBoundaryUiState

    /**
     * Represents the loaded state of the cartographic boundary.
     *
     * @property cartographicBoundary The loaded cartographic boundary.
     */
    data class CartographicBoundaryLoaded(
        val cartographicBoundary: CartographicBoundaryViewData,
    ): CartographicBoundaryUiState
}