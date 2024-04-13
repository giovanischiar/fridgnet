package io.schiar.fridgnet.view.home.util

import androidx.compose.foundation.layout.RowScope
import androidx.compose.runtime.Composable

/**
 * The data class containing information to populate the screen's toolbar.
 *
 * @property title the title of the screen to be displayed.
 * @property actions a composable function that defines the toolbar's content using composables
 * within a `RowScope`. This allows for dynamic toolbar elements based on the screen. (defaults to
 * empty)
 */
data class ScreenInfo(val title: String = "", val actions: @Composable (RowScope.() -> Unit) = {})