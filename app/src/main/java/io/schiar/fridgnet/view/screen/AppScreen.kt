package io.schiar.fridgnet.view.screen

import androidx.compose.foundation.layout.padding
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import io.schiar.fridgnet.R
import io.schiar.fridgnet.view.component.PhotoPicker
import io.schiar.fridgnet.view.screen.administrationunit.AdministrativeUnitScreen
import io.schiar.fridgnet.view.screen.administrationunits.AdministrativeUnitsScreen
import io.schiar.fridgnet.view.screen.map.MapScreen
import io.schiar.fridgnet.view.screen.regionsfromcartographicboundary.RegionsFromCartographicBoundaryScreen
import io.schiar.fridgnet.view.util.BottomNavScreen
import io.schiar.fridgnet.view.util.ScreenInfo
import io.schiar.fridgnet.view.util.chooseWhether
import io.schiar.fridgnet.viewmodel.AdministrativeUnitViewModel
import io.schiar.fridgnet.viewmodel.AdministrativeUnitsViewModel
import io.schiar.fridgnet.viewmodel.AppViewModel
import io.schiar.fridgnet.viewmodel.MapViewModel
import io.schiar.fridgnet.viewmodel.RegionsFromCartographicBoundaryViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppScreen(
    appViewModel: AppViewModel,
    administrativeUnitsViewModel: AdministrativeUnitsViewModel,
    mapViewModel: MapViewModel,
    polygonsViewModel: RegionsFromCartographicBoundaryViewModel,
    administrativeUnitViewModel: AdministrativeUnitViewModel,
    navController: NavHostController = rememberNavController()
) {
    val items = listOf(BottomNavScreen.Home, BottomNavScreen.Map)

    var currentScreenInfo by remember { mutableStateOf(ScreenInfo(BottomNavScreen.Home.route)) }
    var photoPickerShowing by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(currentScreenInfo.title) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = colorResource(id = R.color.indigo_dye_500),
                    titleContentColor = colorResource(id = R.color.white)
                ),
                actions = currentScreenInfo.actions,
                navigationIcon = if (navController.previousBackStackEntry != null) {
                    {
                        IconButton(onClick = { navController.navigateUp() }) {
                            Icon(
                                imageVector = Icons.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = colorResource(id = R.color.white)
                            )
                        }
                    }
                } else {
                    {}
                }
            )
        },

        floatingActionButton = {
            FloatingActionButton(
                onClick = { photoPickerShowing = !photoPickerShowing },
                containerColor = colorResource(id = R.color.imperial_red_500)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_add_photo),
                    contentDescription = "Add photo",
                    tint = colorResource(id = R.color.white)
                )
            }
        },

        bottomBar = {
            BottomNavigation(backgroundColor = colorResource(id = R.color.indigo_dye_500)) {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination
                items.forEach { screen ->
                    val selected = currentDestination?.hierarchy?.any {
                        it.route == screen.route
                    } == true

                    BottomNavigationItem(
                        icon = {
                            screen.icon?.chooseWhether(isSelected = selected)?.let {
                                Icon(
                                    imageVector = it,
                                    contentDescription = null,
                                    tint = colorResource(id = R.color.white)
                                )
                            }
                        },
                        label = {
                            Text(
                                stringResource(screen.resourceId),
                                color = colorResource(id = R.color.white)
                            )
                        },
                        selected = selected,
                        onClick = {
                            navController.navigate(screen.route) {
                                // Pop up to the start destination of the graph to
                                // avoid building up a large stack of destinations
                                // on the back stack as users select items
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                // Avoid multiple copies of the same destination when
                                // reelecting the same item
                                launchSingleTop = true
                                // Restore state when reelecting a previously selected item
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = BottomNavScreen.Home.route,
            Modifier.padding(innerPadding)
        ) {
            composable(route = BottomNavScreen.Home.route) {
                AdministrativeUnitsScreen(
                    viewModel = administrativeUnitsViewModel,
                    onNavigateImage = { navController.navigate("AdministrativeUnit") },
                    info = { screenInfo -> currentScreenInfo = screenInfo }
                )
            }

            composable(route = BottomNavScreen.Map.route) {
                MapScreen(
                    viewModel = mapViewModel,
                    onNavigateRegionsFromCartographicBoundary = {
                        navController.navigate("RegionsFromCartographicBoundary")
                    },
                    info = { screenInfo -> currentScreenInfo = screenInfo }
                )
            }

            composable(route = "AdministrativeUnit") {
                AdministrativeUnitScreen(
                    viewModel = administrativeUnitViewModel,
                    info = { screenInfo -> currentScreenInfo = screenInfo }
                )
            }

            composable(route = "RegionsFromCartographicBoundary") {
                RegionsFromCartographicBoundaryScreen(
                    viewModel = polygonsViewModel,
                    info = { screenInfo -> currentScreenInfo = screenInfo }
                )
            }
        }
    }

    fun onURIsReady(uris: List<String>) {
        appViewModel.addURIs(uris = uris)
        photoPickerShowing = false
    }

    if (photoPickerShowing) {
        PhotoPicker { uris -> onURIsReady(uris = uris) }
    }
}