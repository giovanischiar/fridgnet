package io.schiar.fridgnet.view

import androidx.compose.foundation.layout.padding
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import io.schiar.fridgnet.view.screen.HomeScreen
import io.schiar.fridgnet.view.screen.MapScreen
import io.schiar.fridgnet.view.screen.PhotosScreen
import io.schiar.fridgnet.view.screen.PolygonsScreen
import io.schiar.fridgnet.view.util.BottomNavScreen
import io.schiar.fridgnet.view.util.ScreenInfo
import io.schiar.fridgnet.view.util.chooseWhether
import io.schiar.fridgnet.viewmodel.*
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FridgeApp(
    mainViewModel: MainViewModel,
    homeViewModel: HomeViewModel,
    mapViewModel: MapViewModel,
    polygonsViewModel: PolygonsViewModel,
    photosViewModel: PhotosViewModel,
    navController: NavHostController = rememberNavController()
) {
    val items = listOf(BottomNavScreen.Home, BottomNavScreen.Map)

    var currentScreenInfo by remember { mutableStateOf(ScreenInfo(BottomNavScreen.Home.route)) }
    var photoPickerShowing by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) { mainViewModel.loadDatabase() }
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        mainViewModel.databaseLoaded.collectLatest {
            if (it) { snackbarHostState.showSnackbar(message = "Database Loaded!") }
        }
    }

    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },

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
                        label = { Text(
                            stringResource(screen.resourceId),
                            color = colorResource(id = R.color.white)
                        ) },
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
                HomeScreen(
                    viewModel = homeViewModel,
                    onNavigateImage = {
                        photosViewModel.updateCurrentImages()
                        navController.navigate("Photos")
                    },
                    info = { screenInfo ->  currentScreenInfo = screenInfo }
                )
            }

            composable(route = BottomNavScreen.Map.route) {
                MapScreen(
                    viewModel = mapViewModel,
                    onNavigatePolygons = {
                        polygonsViewModel.updateCurrentLocation()
                        navController.navigate("Polygons")
                    },
                    info = { screenInfo ->  currentScreenInfo = screenInfo }
                )
            }

            composable(route = "Photos") {
                PhotosScreen(
                    viewModel = photosViewModel,
                    info = { screenInfo ->  currentScreenInfo = screenInfo }
                )
            }

            composable(route = "Polygons") {
                PolygonsScreen(
                    viewModel = polygonsViewModel,
                    info = { screenInfo ->  currentScreenInfo = screenInfo }
                )
            }
        }
    }

    val coroutineScope = rememberCoroutineScope()

    fun onURIsReady(uris: List<String>) {
        coroutineScope.launch { mainViewModel.addURIs(uris = uris) }
        photoPickerShowing = false
    }

    if (photoPickerShowing) { PhotoPicker { uris -> onURIsReady(uris = uris) } }
}