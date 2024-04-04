package io.schiar.fridgnet.view.screen.map

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import io.schiar.fridgnet.R
import io.schiar.fridgnet.view.screen.map.component.Map
import io.schiar.fridgnet.view.screen.map.component.TopAppBarActionButton
import io.schiar.fridgnet.view.util.ScreenInfo
import io.schiar.fridgnet.view.util.toBoundingBoxViewData
import io.schiar.fridgnet.viewmodel.MapViewModel

@Composable
fun MapScreen(
    viewModel: MapViewModel = hiltViewModel(),
    onNavigateRegionsFromCartographicBoundary: () -> Unit,
    info: (screenInfo: ScreenInfo) -> Unit
) {
    var zoomCameraToFitImages by remember { mutableStateOf(false) }
    val visibleImages by viewModel.visibleImagesFlow.collectAsState(initial = emptyList())
    val visibleRegions by viewModel.visibleRegionsFlow.collectAsState(initial = emptyList())
    val boundingBoxImages by viewModel.boundingBoxImagesFlow.collectAsState(initial = null)

    info(
        ScreenInfo(
            title = stringResource(id = R.string.map_screen),
            actions = {
                TopAppBarActionButton(
                    iconResId = R.drawable.ic_fit_screen,
                    description = "Zoom to fit",
                    enabled = !zoomCameraToFitImages
                ) {
                    zoomCameraToFitImages = true
                }
            }
        )
    )

    Map(
        modifier = Modifier.fillMaxSize(),
        visibleImages = visibleImages,
        visibleRegions = visibleRegions,
        boundingBox = boundingBoxImages,
        zoomCameraToFitImages = zoomCameraToFitImages,
        onMoveFinished = { zoomCameraToFitImages = false },
        regionPressedAt = { index ->
            viewModel.selectRegionAt(index = index)
            onNavigateRegionsFromCartographicBoundary()
        },
        onVisibleMapRegionChangeTo = { latLngBounds ->
            val bounds = latLngBounds.toBoundingBoxViewData()
            viewModel.visibleAreaChanged(boundingBoxViewData = bounds)
        }
    )
}