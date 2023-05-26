package io.schiar.fridgnet.view.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import io.schiar.fridgnet.R
import io.schiar.fridgnet.view.component.Map
import io.schiar.fridgnet.view.component.TopAppBarActionButton
import io.schiar.fridgnet.view.util.ScreenInfo
import io.schiar.fridgnet.view.util.toBoundingBoxViewData
import io.schiar.fridgnet.viewmodel.MainViewModel

@Composable
fun MapScreen(
    viewModel: MainViewModel,
    onNavigatePolygons: () -> Unit,
    info: (screenInfo: ScreenInfo) -> Unit
) {
    var moveCamera by remember { mutableStateOf(false) }
    val visibleImages by viewModel.visibleImages.collectAsState()
    val visibleRegions by viewModel.visibleRegions.collectAsState()
    val allPhotosBoundingBox by viewModel.allPhotosBoundingBox.collectAsState()

    info(
        ScreenInfo(
            title = stringResource(id = R.string.map_screen),
            actions = {
                TopAppBarActionButton(
                    iconResId = R.drawable.ic_fit_screen,
                    description = "Zoom to fit",
                    enabled = !moveCamera
                ) {
                    moveCamera = true
                }
            }
        )
    )

    Column {
        Box(modifier = Modifier.fillMaxSize()) {
            Map(
                modifier = Modifier.fillMaxSize(),
                visibleImages = visibleImages,
                visibleRegions = visibleRegions,
                boundingBox = allPhotosBoundingBox,
                moveCamera = moveCamera,
                onMoveFinished = { moveCamera = false },
                onClickRegion = { region ->
                    viewModel.selectRegion(regionViewData = region)
                    onNavigatePolygons()
                }
            ) { latLngBounds ->
                if (latLngBounds != null) {
                    val bounds = latLngBounds.toBoundingBoxViewData()
                    viewModel.visibleAreaChanged(boundingBoxViewData = bounds)
                }
            }
        }
    }
}