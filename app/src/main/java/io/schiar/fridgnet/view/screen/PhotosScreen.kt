package io.schiar.fridgnet.view.screen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.stringResource
import io.schiar.fridgnet.R
import io.schiar.fridgnet.view.component.PhotoGrid
import io.schiar.fridgnet.view.util.ScreenInfo
import io.schiar.fridgnet.viewmodel.MainViewModel

@Composable
fun PhotosScreen(
    viewModel: MainViewModel,
    info: (screenInfo: ScreenInfo) -> Unit
) {
    info(
        ScreenInfo(title = stringResource(id = R.string.photos_screen))
    )

    val selectedImages by viewModel.selectedImages.collectAsState()
    PhotoGrid(images = selectedImages)
}