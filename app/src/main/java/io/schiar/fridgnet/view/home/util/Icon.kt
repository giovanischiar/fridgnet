package io.schiar.fridgnet.view.home.util

import androidx.compose.ui.graphics.vector.ImageVector

/**
 * The class that encapsulates data to display an icon with selectable states.
 *
 * @property selected the icon displayed in the active state.
 * @property unselected the icon displayed in the inactive state.
 * @property contentDescriptionStringID the string resource ID for the content description
 *  (supports internationalization).
 */
data class Icon(
    val selected: ImageVector,
    val unselected: ImageVector,
    val contentDescriptionStringID: Int
) {
    /**
     * Switch between both selected of unselected state using a boolean.
     *
     * @param isSelected the boolean used to choose between states.
     * @return the [ImageVector] of the icon state corresponding.
     */
    fun chooseWhether(isSelected: Boolean): ImageVector {
        return if (isSelected) selected else unselected
    }
}