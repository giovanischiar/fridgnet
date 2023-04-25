package io.schiar.fridgnet.view

import androidx.compose.foundation.layout.padding
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import io.schiar.fridgnet.view.screen.HomeScreen
import io.schiar.fridgnet.view.screen.MapScreen
import io.schiar.fridgnet.viewmodel.MainViewModel

@Composable
fun FridgeApp(viewModel: MainViewModel, navController: NavHostController = rememberNavController()) {
    val items = listOf(Screen.Home, Screen.Map)

    Scaffold(
        bottomBar = {
            BottomNavigation {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination
                items.forEach { screen ->
                    val selected = currentDestination?.hierarchy?.any {
                        it.route == screen.route
                    } == true

                    BottomNavigationItem(
                        icon = { Icon(
                            imageVector = screen.icon.chooseWhether(isSelected = selected),
                            contentDescription = null,
                            tint = Color.White
                        ) },
                        label = { Text(
                            stringResource(screen.resourceId),
                            color = Color.White
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
            startDestination = Screen.Home.route,
            Modifier.padding(innerPadding)
        ) {
            composable(route = Screen.Home.route) { HomeScreen(viewModel = viewModel) }
            composable(route = Screen.Map.route) { MapScreen(viewModel = viewModel) }
        }
    }
}