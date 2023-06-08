package io.schiar.fridgnet.view.screen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import io.schiar.fridgnet.view.component.PhotoGrid
import io.schiar.fridgnet.view.util.ScreenInfo
import io.schiar.fridgnet.viewmodel.HomeViewModel

@Composable
fun PhotosScreen(
    viewModel: HomeViewModel,
    info: (screenInfo: ScreenInfo) -> Unit
) {
    val selectedImages by viewModel.selectedImages.collectAsState()
    val (title, images) = selectedImages

    info(
        ScreenInfo(title = title)
    )
    PhotoGrid(images = images)
}