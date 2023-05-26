package io.schiar.fridgnet.view.util

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Place
import io.schiar.fridgnet.R

sealed class BottomNavScreen(val route: String, @StringRes val resourceId: Int, val icon: Icon? = null) {
    object Home : BottomNavScreen(
        route = "home",
        resourceId = R.string.home_screen,
        icon = Icon (
            selected = Icons.Filled.Home,
            unselected = Icons.Outlined.Home
        )
    )
    object Map : BottomNavScreen(
        route = "map",
        resourceId = R.string.map_screen,
        icon = Icon(
            selected = Icons.Filled.Place,
            unselected = Icons.Outlined.Place
        )
    )
}