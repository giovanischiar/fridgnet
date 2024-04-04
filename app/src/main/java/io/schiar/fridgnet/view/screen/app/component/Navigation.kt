package io.schiar.fridgnet.view.screen.app.component

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import io.schiar.fridgnet.view.screen.administrationunit.AdministrativeUnitScreen
import io.schiar.fridgnet.view.screen.administrationunits.AdministrativeUnitsScreen
import io.schiar.fridgnet.view.screen.map.MapScreen
import io.schiar.fridgnet.view.screen.regionsfromcartographicboundary.RegionsFromCartographicBoundaryScreen
import io.schiar.fridgnet.view.util.BottomNavScreen
import io.schiar.fridgnet.view.util.ScreenInfo

@Composable
fun Navigation(
    navController: NavHostController,
    innerPadding: PaddingValues,
    onChangeScreenInfo: (screenInfo: ScreenInfo) -> Unit
) {
    NavHost(
        modifier = Modifier.padding(innerPadding),
        navController = navController,
        startDestination = BottomNavScreen.Home.route
    ) {
        composable(route = BottomNavScreen.Home.route) {
            AdministrativeUnitsScreen(
                onNavigateImage = { navController.navigate("AdministrativeUnit") },
                info = onChangeScreenInfo
            )
        }

        composable(route = BottomNavScreen.Map.route) {
            MapScreen(
                onNavigateRegionsFromCartographicBoundary = {
                    navController.navigate("RegionsFromCartographicBoundary")
                },
                info = onChangeScreenInfo
            )
        }

        composable(route = "AdministrativeUnit") {
            AdministrativeUnitScreen(info = onChangeScreenInfo)
        }

        composable(route = "RegionsFromCartographicBoundary") {
            RegionsFromCartographicBoundaryScreen(info = onChangeScreenInfo)
        }
    }
}