package io.schiar.fridgnet.view.home.component

import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import io.schiar.fridgnet.R

/**
 * A floating action button typically used to initiate adding a new image across various screens in
 * the application.
 *
 * @param backgroundColorID The color ID of the background.
 * @param iconDrawableID The icon drawable ID.
 * @param iconTintColorID The tint color ID of the icon.
 * @param contentDescriptionStringID The content description string ID.
 * @param onPress The event triggered when the button is pressed.
 */
@Composable
fun FloatingActionButton(
    backgroundColorID: Int = R.color.imperial_red_500,
    iconDrawableID: Int,
    iconTintColorID: Int = R.color.white,
    contentDescriptionStringID: Int,
    onPress: () -> Unit
) {
    androidx.compose.material3.FloatingActionButton(
        onClick = onPress,
        containerColor = colorResource(id = backgroundColorID)
    ) {
        Icon(
            painter = painterResource(id = iconDrawableID),
            contentDescription = stringResource(id = contentDescriptionStringID),
            tint = colorResource(id = iconTintColorID)
        )
    }
}