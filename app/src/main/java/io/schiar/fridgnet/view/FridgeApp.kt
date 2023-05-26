package io.schiar.fridgnet.view

import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.padding
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
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
import io.schiar.fridgnet.view.screen.HomeScreen
import io.schiar.fridgnet.view.screen.MapScreen
import io.schiar.fridgnet.view.screen.PhotosScreen
import io.schiar.fridgnet.view.screen.PolygonsScreen
import io.schiar.fridgnet.view.util.AddressCreator
import io.schiar.fridgnet.view.util.Screen
import io.schiar.fridgnet.view.util.chooseWhether
import io.schiar.fridgnet.viewmodel.MainViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FridgeApp(viewModel: MainViewModel, navController: NavHostController = rememberNavController()) {
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    val items = listOf(Screen.Home, Screen.Map)

    var currentScreen by remember { mutableStateOf(items[0]) }
    var currentActions by remember { mutableStateOf<@Composable (RowScope.() -> Unit)>(value = {}) }
    var photoPickerShowing by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(id = currentScreen.resourceId)) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = colorResource(id = R.color.indigo_dye_500),
                    titleContentColor = colorResource(id = R.color.white)
                ),
                actions = currentActions
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

                    if (selected) { currentScreen = screen }

                    BottomNavigationItem(
                        icon = { Icon(
                            imageVector = screen.icon.chooseWhether(isSelected = selected),
                            contentDescription = null,
                            tint = colorResource(id = R.color.white)
                        ) },
                        label = { Text(
                            stringResource(screen.resourceId),
                            color = colorResource(id = R.color.white)
                        ) },
                        selected = selected,
                        onClick = {
                            currentActions = {}
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
            startDestination = Screen.Home.route,
            Modifier.padding(innerPadding)
        ) {
            composable(route = Screen.Home.route) {
                HomeScreen(
                    viewModel = viewModel,
                    onNavigateImage = { navController.navigate("Photos") },
                    onActions = { actions -> currentActions = actions }
                )
            }

            composable(route = Screen.Map.route) {
                MapScreen(
                    viewModel = viewModel,
                    onNavigatePolygons = { navController.navigate("Polygons") },
                    onActions = { actions -> currentActions = actions }
                )
            }
            composable(route = "Photos") { PhotosScreen(viewModel = viewModel) }
            composable(route = "Polygons") { PolygonsScreen(viewModel = viewModel) }
        }
    }

    if (photoPickerShowing) {
        PhotoPicker { uri, date, latitude, longitude ->
            viewModel.addImage(
                uri = uri,
                date = date,
                latitude = latitude,
                longitude = longitude
            )

            coroutineScope.launch(Dispatchers.IO) {
                val address = withContext(Dispatchers.Default) {
                    AddressCreator().addressFromLocation(
                        context = context,
                        latitude = latitude,
                        longitude = longitude
                    )
                }

                viewModel.addAddressToImage(
                    uri = uri,
                    locality = address.locality,
                    subAdminArea = address.subAdminArea,
                    adminArea = address.adminArea,
                    countryName = address.countryName
                )
            }

            photoPickerShowing = false
        }
    }
}