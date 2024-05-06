package io.schiar.fridgnet.view.home.component

import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import io.schiar.fridgnet.R
import io.schiar.fridgnet.view.home.util.Screen

/**
 * This component is responsible for defining the screens that will be displayed at a bar
 * of all screens.
 *
 * @param screens The screens considered to be used on the bar.
 * @param navController The object used to navigate from one screen to another
 */
@Composable
fun ScreenBarNavigator(
    screens: List<Screen>,
    navController: NavHostController
) {
    NavigationBar(containerColor = colorResource(id = R.color.indigo_dye_500)) {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentDestination = navBackStackEntry?.destination
        screens.forEach { screen ->
            val selected = currentDestination?.hierarchy?.any {
                it.route == screen.route.id
            } == true

            NavigationBarItem(
                icon = {
                    screen.icon?.chooseWhether(isSelected = selected)?.let { imageVector ->
                        Icon(
                            imageVector = imageVector,
                            contentDescription = stringResource(
                                id = screen.icon.contentDescriptionStringID
                            ),
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
                    navController.navigate(screen.route.id) {
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