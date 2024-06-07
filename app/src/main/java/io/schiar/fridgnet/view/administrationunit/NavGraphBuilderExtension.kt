package io.schiar.fridgnet.view.administrationunit

import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import io.schiar.fridgnet.view.home.util.Route
import io.schiar.fridgnet.view.home.util.ScreenInfo
import io.schiar.fridgnet.viewmodel.AdministrativeUnitViewModel

/**
 * Adds a composable destination for the Administrative Unit Screen to the navigation graph.
 *
 * This function sets up the navigation route for the Administrative Unit Screen, injecting
 * the required ViewModel and observing its UI state. It then displays the screen with the
 * appropriate UI state and toolbar information.
 *
 * @param onChangeScreenInfo A callback function that updates the top bar components with
 * the provided screen information (e.g., title, actions).
 */
fun NavGraphBuilder.administrativeUnitScreen(
    onChangeScreenInfo: (screenInfo: ScreenInfo) -> Unit,
) {
    composable(route = Route.ADMINISTRATIVE_UNIT.id) {
        val viewModel = hiltViewModel<AdministrativeUnitViewModel>()
        val administrativeUnitUiState by viewModel
            .currentAdministrativeUnitUiStateFlow
            .collectAsState(initial = CurrentAdministrativeUnitUiState.Loading)
        AdministrativeUnitScreen(
            onSetToolbarInfo = onChangeScreenInfo,
            administrativeUnitUiState = administrativeUnitUiState

        )
    }
}