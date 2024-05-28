package io.schiar.fridgnet.view.administrationunits

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
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
        AdministrativeUnitsScreen(
            onNavigateToAdministrativeUnit = onNavigateToAdministrativeUnit,
            onSetToolbarInfo = onChangeScreenInfo,
            // TODO extract ui state and pass only the state to the screen. See AdministrativeUnitScreen
            viewModel = viewModel
        )
    }
}