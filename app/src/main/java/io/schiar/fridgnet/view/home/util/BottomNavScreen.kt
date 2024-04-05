package io.schiar.fridgnet.view.home.util

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Place
import io.schiar.fridgnet.R

sealed class BottomNavScreen(
    val route: Route,
    @StringRes val resourceId: Int,
    val icon: Icon? = null
) {
    data object AdministrativeUnits : BottomNavScreen(
        route = Route.ADMINISTRATIVE_UNITS,
        resourceId = R.string.administrative_units_screen,
        icon = Icon(
            selected = Icons.Filled.Home,
            unselected = Icons.Outlined.Home,
            contentDescriptionStringID = R.string.administrative_units_screen
        )
    )

    data object RegionsAndImages : BottomNavScreen(
        route = Route.REGIONS_AND_IMAGES,
        resourceId = R.string.regions_and_images_screen,
        icon = Icon(
            selected = Icons.Filled.Place,
            unselected = Icons.Outlined.Place,
            contentDescriptionStringID = R.string.regions_and_images_screen
        )
    )
}