package io.schiar.fridgnet.view.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import io.schiar.fridgnet.view.administrationunit.administrativeUnitScreen
import io.schiar.fridgnet.view.administrationunits.administrativeUnitsScreen
import io.schiar.fridgnet.view.home.util.Route
import io.schiar.fridgnet.view.home.util.Screen
import io.schiar.fridgnet.view.home.util.ScreenInfo
import io.schiar.fridgnet.view.regionsandimages.regionsAndImagesScreen
import io.schiar.fridgnet.view.regionsfromcartographicboundary.RegionsFromCartographicBoundaryScreen

/**
 * This composable manages screen navigation within the application based on user actions. It
 * utilizes a `NavHost` to handle screen transitions and provides callbacks for updating the toolbar
 * information associated with each screen.
 *
 * @param navController the component used to navigate between screens.
 * @param innerPadding the padding values typically provided by the scaffold function in the Home
 * Screen.
 * @param onChangeToolbarInfo an event fired whenever the screen information (title, toolbar
 * components) needs to be updated. This typically happens when switching screens.
 */
@Composable
fun Navigation(
    navController: NavHostController,
    innerPadding: PaddingValues,
    onChangeToolbarInfo: (screenInfo: ScreenInfo) -> Unit
) {
    NavHost(
        modifier = Modifier.padding(innerPadding),
        navController = navController,
        startDestination = Screen.AdministrativeUnits.route.id
    ) {
        administrativeUnitsScreen(
            onNavigateToAdministrativeUnit = {
                navController.navigate(route = Route.ADMINISTRATIVE_UNIT.id)
            },
            onChangeScreenInfo = onChangeToolbarInfo
        )

        regionsAndImagesScreen(
            onNavigateToRegionsFromCartographicBoundary = {
                navController.navigate(route = Route.REGIONS_FROM_CARTOGRAPHIC_BOUNDARY.id)
            },
            onChangeToolbarInfo = onChangeToolbarInfo
        )

        administrativeUnitScreen(onChangeScreenInfo = onChangeToolbarInfo)

        composable(route = Route.REGIONS_FROM_CARTOGRAPHIC_BOUNDARY.id) {
            RegionsFromCartographicBoundaryScreen(onSetToolbarInfo = onChangeToolbarInfo)
        }
    }
}