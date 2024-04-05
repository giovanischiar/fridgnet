package io.schiar.fridgnet.view.home.component

import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import io.schiar.fridgnet.R
import io.schiar.fridgnet.view.home.util.BottomNavScreen
import io.schiar.fridgnet.view.home.util.chooseWhether

@Composable
fun BottomBar(navController: NavHostController) {
    val bottomNavScreens = remember {
        listOf(BottomNavScreen.AdministrativeUnits, BottomNavScreen.RegionsAndImages)
    }
    NavigationBar(containerColor = colorResource(id = R.color.indigo_dye_500)) {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentDestination = navBackStackEntry?.destination
        bottomNavScreens.forEach { bottomNavScreen ->
            val selected = currentDestination?.hierarchy?.any {
                it.route == bottomNavScreen.route.id
            } == true

            NavigationBarItem(
                icon = {
                    bottomNavScreen.icon?.chooseWhether(isSelected = selected)?.let { imageVector ->
                        Icon(
                            imageVector = imageVector,
                            contentDescription = stringResource(
                                id = bottomNavScreen.icon.contentDescriptionStringID
                            ),
                            tint = colorResource(id = R.color.white)
                        )
                    }
                },
                label = {
                    Text(
                        stringResource(bottomNavScreen.resourceId),
                        color = colorResource(id = R.color.white)
                    )
                },
                selected = selected,
                onClick = {
                    navController.navigate(bottomNavScreen.route.id) {
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