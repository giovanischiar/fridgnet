package io.schiar.fridgnet.view.map.component

import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.vectorResource
import io.schiar.fridgnet.R

@Composable
fun TopAppBarActionButton(
    iconResId: Int,
    description: String,
    enabled: Boolean = true,
    onClick: () -> Unit,
) {
    IconButton(onClick = {
        onClick()
    }, enabled = enabled) {
        val imageVector = ImageVector.vectorResource(id = iconResId)

        Icon(
            imageVector = imageVector,
            contentDescription = description,
            tint = colorResource(id = R.color.white)
        )
    }
}