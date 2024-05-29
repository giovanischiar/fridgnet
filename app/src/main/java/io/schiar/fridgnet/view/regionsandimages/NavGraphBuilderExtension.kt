package io.schiar.fridgnet.view.regionsandimages

import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import io.schiar.fridgnet.view.home.util.Screen
import io.schiar.fridgnet.view.home.util.ScreenInfo
import io.schiar.fridgnet.view.regionsandimages.uistate.BoundingBoxImagesUiState
import io.schiar.fridgnet.view.regionsandimages.uistate.VisibleImagesUiState
import io.schiar.fridgnet.view.regionsandimages.uistate.VisibleRegionsUiState
import io.schiar.fridgnet.viewmodel.RegionsAndImagesViewModel

/**
 * Adds a composable destination for the Administrative Unit Screen to the navigation graph.
 *
 * This function sets up the navigation route for the Administrative Units Screen, injecting
 * the required ViewModel and observing its UI state. It then displays the screen with the
 * appropriate UI state and toolbar information.
 *
 * @param onNavigateToRegionsFromCartographicBoundary A callback function that triggers the
 * navigation to the Regions From Cartographic Boundary screen.
 * @param onChangeToolbarInfo A callback function that updates the top bar components with
 * the provided screen information (e.g., title, actions).
 */
fun NavGraphBuilder.regionsAndImagesScreen(
    onNavigateToRegionsFromCartographicBoundary: () -> Unit,
    onChangeToolbarInfo: (screenInfo: ScreenInfo) -> Unit
) {
    composable(route = Screen.RegionsAndImages.route.id) {
        val viewModel = hiltViewModel<RegionsAndImagesViewModel>()
        val visibleRegionsUiState by viewModel.visibleRegionsUiStateFlow
            .collectAsState(initial = VisibleRegionsUiState.Loading)
        val visibleImagesUiState by viewModel.visibleImagesUiStateFlow
            .collectAsState(initial = VisibleImagesUiState.Loading)
        val boundingBoxImagesUiState by viewModel.boundingBoxImagesUiStateFlow
            .collectAsState(initial = BoundingBoxImagesUiState.Loading)

        RegionsAndImagesScreen(
            visibleRegionsUiState = visibleRegionsUiState,
            visibleImagesUiState = visibleImagesUiState,
            boundingBoxImagesUiState = boundingBoxImagesUiState,
            selectRegionAt = viewModel::selectRegionAt,
            visibleAreaChanged = viewModel::visibleAreaChanged,
            onNavigateToRegionsFromCartographicBoundary
                = onNavigateToRegionsFromCartographicBoundary,
            onChangeToolbarInfo = onChangeToolbarInfo
        )
    }
}