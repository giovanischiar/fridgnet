package io.schiar.fridgnet.view.util

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Place
import io.schiar.fridgnet.R

sealed class Screen(val route: String, @StringRes val resourceId: Int, val icon: Icon) {
    object Home : Screen(
        route = "home",
        resourceId = R.string.home,
        icon = Icon (
            selected = Icons.Filled.Home,
            unselected = Icons.Outlined.Home
        )
    )
    object Map : Screen(
        route = "map",
        resourceId = R.string.map,
        icon = Icon(
            selected = Icons.Filled.Place,
            unselected = Icons.Outlined.Place
        )
    )
}