package io.schiar.fridgnet.view.regionsandimages.component

import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.vectorResource
import io.schiar.fridgnet.R

/**
 * Component responsible to display a button as icon.
 *
 * @param iconResId the icon id from resources.
 * @param description the description of the icon action for accessibility.
 * @param enabled whether the icon is enable.
 * @param onPressed fired then whe button was pressed.
 */
@Composable
fun TopAppBarActionButton(
    iconResId: Int,
    description: String,
    enabled: Boolean = true,
    onPressed: () -> Unit,
) {
    IconButton(onClick = onPressed, enabled = enabled) {
        val imageVector = ImageVector.vectorResource(id = iconResId)

        Icon(
            imageVector = imageVector,
            contentDescription = description,
            tint = colorResource(id = R.color.white)
        )
    }
}