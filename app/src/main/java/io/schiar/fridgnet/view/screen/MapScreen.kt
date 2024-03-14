package io.schiar.fridgnet.view.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import io.schiar.fridgnet.R
import io.schiar.fridgnet.view.component.Map
import io.schiar.fridgnet.view.component.TopAppBarActionButton
import io.schiar.fridgnet.view.util.ScreenInfo
import io.schiar.fridgnet.view.util.toBoundingBoxViewData
import io.schiar.fridgnet.viewmodel.MapViewModel

@Composable
fun MapScreen(
    viewModel: MapViewModel,
    onNavigatePolygons: () -> Unit,
    info: (screenInfo: ScreenInfo) -> Unit
) {
    var moveCamera by remember { mutableStateOf(false) }
    val visibleImages by viewModel.visibleImages.collectAsState(initial = emptyList())
    val visibleRegions by viewModel.visibleRegions.collectAsState(initial = emptyList())
    val boundingBoxImages by viewModel.boundingBoxImages.collectAsState(initial = null)

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
                boundingBox = boundingBoxImages,
                moveCamera = moveCamera,
                onMoveFinished = { moveCamera = false },
                regionPressedAt = { index -> run {
                        viewModel.selectRegionAt(index = index)
                        onNavigatePolygons()
                    }
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