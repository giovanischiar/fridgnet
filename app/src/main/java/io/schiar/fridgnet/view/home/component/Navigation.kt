package io.schiar.fridgnet.view.home.component

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import io.schiar.fridgnet.view.administrationunit.AdministrativeUnitScreen
import io.schiar.fridgnet.view.administrationunits.AdministrativeUnitsScreen
import io.schiar.fridgnet.view.home.util.BottomNavScreen
import io.schiar.fridgnet.view.home.util.Route
import io.schiar.fridgnet.view.home.util.ScreenInfo
import io.schiar.fridgnet.view.regionsandimages.RegionsAndImagesScreen
import io.schiar.fridgnet.view.regionsfromcartographicboundary.RegionsFromCartographicBoundaryScreen

@Composable
fun Navigation(
    navController: NavHostController,
    innerPadding: PaddingValues,
    onChangeScreenInfo: (screenInfo: ScreenInfo) -> Unit
) {
    NavHost(
        modifier = Modifier.padding(innerPadding),
        navController = navController,
        startDestination = BottomNavScreen.AdministrativeUnits.route.id
    ) {
        composable(route = BottomNavScreen.AdministrativeUnits.route.id) {
            AdministrativeUnitsScreen(
                onNavigateToAdministrativeUnit = {
                    navController.navigate(route = Route.ADMINISTRATIVE_UNIT.id)
                },
                info = onChangeScreenInfo
            )
        }

        composable(route = BottomNavScreen.RegionsAndImages.route.id) {
            RegionsAndImagesScreen(
                onNavigateToRegionsFromCartographicBoundary = {
                    navController.navigate(route = Route.REGIONS_FROM_CARTOGRAPHIC_BOUNDARY.id)
                },
                info = onChangeScreenInfo
            )
        }

        composable(route = Route.ADMINISTRATIVE_UNIT.id) {
            AdministrativeUnitScreen(info = onChangeScreenInfo)
        }

        composable(route = Route.REGIONS_FROM_CARTOGRAPHIC_BOUNDARY.id) {
            RegionsFromCartographicBoundaryScreen(info = onChangeScreenInfo)
        }
    }
}