package io.schiar.fridgnet.view.app.component

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import io.schiar.fridgnet.view.administrationunit.AdministrativeUnitScreen
import io.schiar.fridgnet.view.administrationunits.AdministrativeUnitsScreen
import io.schiar.fridgnet.view.app.util.BottomNavScreen
import io.schiar.fridgnet.view.app.util.ScreenInfo
import io.schiar.fridgnet.view.map.MapScreen
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