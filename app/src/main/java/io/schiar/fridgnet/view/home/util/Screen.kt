package io.schiar.fridgnet.view.home.util

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Place
import io.schiar.fridgnet.R

/**
 * The class used to encapsulate the data needed to display screen information in the bottom bar
 * of the home screen.
 *
 * @property route the [Route] associated with the screen.
 * @property resourceId the string resource ID of the screen's name in the bottom bar.
 * @property icon the icon to be displayed for the screen (optional).
 */
sealed class Screen(
    val route: Route,
    @StringRes val resourceId: Int,
    val icon: Icon? = null
) {
    /**
     * The object that represents the Administrative Units Screen's Bottom Menu Option.
     */
    data object AdministrativeUnits : Screen(
        route = Route.ADMINISTRATIVE_UNITS,
        resourceId = R.string.administrative_units_screen,
        icon = Icon(
            selected = Icons.Filled.Home,
            unselected = Icons.Outlined.Home,
            contentDescriptionStringID = R.string.administrative_units_screen
        )
    )

    /**
     * The object that represents the Regions And Images Screen's Bottom Menu Option.
     */
    data object RegionsAndImages : Screen(
        route = Route.REGIONS_AND_IMAGES,
        resourceId = R.string.regions_and_images_screen,
        icon = Icon(
            selected = Icons.Filled.Place,
            unselected = Icons.Outlined.Place,
            contentDescriptionStringID = R.string.regions_and_images_screen
        )
    )
}