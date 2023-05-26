package io.schiar.fridgnet.view.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import io.schiar.fridgnet.R
import io.schiar.fridgnet.view.component.Map
import io.schiar.fridgnet.view.component.TopAppBarActionButton
import io.schiar.fridgnet.view.util.toBoundingBoxViewData
import io.schiar.fridgnet.viewmodel.MainViewModel

@Composable
fun MapScreen(
    viewModel: MainViewModel,
    onNavigatePolygons: () -> Unit,
    onActions: (actions: @Composable (RowScope.() -> Unit)) -> Unit
) {
    var moveCamera by remember { mutableStateOf(false) }
    val visibleImages by viewModel.visibleImages.collectAsState()
    val visibleRegions by viewModel.visibleRegions.collectAsState()
    val allPhotosBoundingBox by viewModel.allPhotosBoundingBox.collectAsState()

    onActions {
        TopAppBarActionButton(
            iconResId = R.drawable.ic_fit_screen,
            description = "Zoom to fit",
            enabled = !moveCamera
        ) {
            moveCamera = true
        }
    }

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