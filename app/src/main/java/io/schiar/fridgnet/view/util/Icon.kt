package io.schiar.fridgnet.view.util

import androidx.compose.ui.graphics.vector.ImageVector

data class Icon(
    val selected: ImageVector,
    val unselected: ImageVector,
    val contentDescriptionStringID: Int
)

fun Icon.chooseWhether(isSelected: Boolean): ImageVector {
    return if (isSelected) selected else unselected
}