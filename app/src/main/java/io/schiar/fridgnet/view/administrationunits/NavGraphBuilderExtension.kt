package io.schiar.fridgnet.view.administrationunits

import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import io.schiar.fridgnet.view.administrationunits.uistate.AdministrativeUnitsUiState
import io.schiar.fridgnet.view.home.util.Screen
import io.schiar.fridgnet.view.home.util.ScreenInfo
import io.schiar.fridgnet.viewmodel.AdministrativeUnitsViewModel

/**
 * Adds a composable destination for the Administrative Unit Screen to the navigation graph.
 *
 * This function sets up the navigation route for the Administrative Units Screen, injecting
 * the required ViewModel and observing its UI state. It then displays the screen with the
 * appropriate UI state and toolbar information.
 *
 * @param onNavigateToAdministrativeUnit A callback function that triggers the navigation to the
 * Administrative Unit screen.
 * @param onChangeScreenInfo A callback function that updates the top bar components with
 * the provided screen information (e.g., title, actions).
 */
fun NavGraphBuilder.administrativeUnitsScreen(
    onNavigateToAdministrativeUnit: () -> Unit,
    onChangeScreenInfo: (screenInfo: ScreenInfo) -> Unit,
) {
    composable(route = Screen.AdministrativeUnits.route.id) {
        val viewModel = hiltViewModel<AdministrativeUnitsViewModel>()
        val administrativeUnitsUiState by viewModel.administrativeUnitsUiStateFlow
            .collectAsState(initial = AdministrativeUnitsUiState.Loading)
        val administrativeLevelsUiState by viewModel.administrativeLevelsUiStateFlow
            .collectAsState()
        val currentAdministrativeLevelUiState by viewModel.currentAdministrativeLevelUiStateFlow
            .collectAsState()

        AdministrativeUnitsScreen(
            administrativeUnitsUiState = administrativeUnitsUiState,
            administrativeLevelsUiState = administrativeLevelsUiState,
            currentAdministrativeLevelUiState = currentAdministrativeLevelUiState,
            onDropdownSelectedAt = viewModel::changeCurrentAdministrativeLevel,
            onNavigateToAdministrativeUnit = onNavigateToAdministrativeUnit,
            onRemoveAllImagesButtonPressed = viewModel::removeAllImages,
            onAdministrativeUnitPressedAt = viewModel::selectCartographicBoundaryGeoLocationAt,
            onSetToolbarInfo = onChangeScreenInfo
        )
    }
}