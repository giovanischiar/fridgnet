package io.schiar.fridgnet.view.util

import androidx.compose.foundation.layout.RowScope
import androidx.compose.runtime.Composable

data class ScreenInfo(val title: String = "", val actions: @Composable (RowScope.() -> Unit) = {})