package io.schiar.fridgnet.view.regionsfromcartographicboundary

import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import io.schiar.fridgnet.view.home.util.Route
import io.schiar.fridgnet.view.home.util.ScreenInfo
import io.schiar.fridgnet.view.regionsfromcartographicboundary.uiState.CartographicBoundaryUiState
import io.schiar.fridgnet.viewmodel.RegionsFromCartographicBoundaryViewModel

/**
 * Adds a composable destination for the Administrative Unit Screen to the navigation graph.
 *
 * This function sets up the navigation route for the Administrative Units Screen, injecting
 * the required ViewModel and observing its UI state. It then displays the screen with the
 * appropriate UI state and toolbar information.
 *
 * @param onChangeToolbarInfo A callback function that updates the top bar components with
 * the provided screen information (e.g., title, actions).
 */
fun NavGraphBuilder.regionsFromCartographicBoundaryScreen(
    onChangeToolbarInfo: (screenInfo: ScreenInfo) -> Unit
) {
    composable(route = Route.REGIONS_FROM_CARTOGRAPHIC_BOUNDARY.id) {
        val viewModel = hiltViewModel<RegionsFromCartographicBoundaryViewModel>()
        val cartographicBoundaryUiState by viewModel.currentCartographicBoundaryUiStateFlow
            .collectAsState(initial = CartographicBoundaryUiState.Loading)

        RegionsFromCartographicBoundaryScreen(
            cartographicBoundaryUiState = cartographicBoundaryUiState,
            switchAll = viewModel::switchAll,
            switchRegionAt = viewModel::switchRegionAt,
            onChangeToolbarInfo = onChangeToolbarInfo
        )
    }
}