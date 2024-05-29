package io.schiar.fridgnet.view.regionsandimages

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import io.schiar.fridgnet.R
import io.schiar.fridgnet.view.home.util.ScreenInfo
import io.schiar.fridgnet.view.regionsandimages.component.Map
import io.schiar.fridgnet.view.regionsandimages.component.TopAppBarActionButton
import io.schiar.fridgnet.view.regionsandimages.uistate.BoundingBoxImagesUiState
import io.schiar.fridgnet.view.regionsandimages.uistate.VisibleImagesUiState
import io.schiar.fridgnet.view.regionsandimages.uistate.VisibleRegionsUiState
import io.schiar.fridgnet.view.shared.util.toBoundingBoxViewData
import io.schiar.fridgnet.view.shared.viewdata.BoundingBoxViewData

/**
 * The component representing the Regions and Images Screen. It displays a map with plotted images
 * and regions, and allows zooming functionality.
 *
 * @param visibleRegionsUiState The UI state for visible regions, which can be either loading or
 * loaded.
 * @param visibleImagesUiState The UI state for visible images, which can be either loading or
 * loaded.
 * @param boundingBoxImagesUiState The UI state for the images within the current bounding box.
 * @param selectRegionAt A callback function to be invoked when a region is selected, with the index
 * of the selected region.
 * @param visibleAreaChanged A callback function to be invoked when the visible area of the map
 * changes, passing the new bounding box data.
 * @param onNavigateToRegionsFromCartographicBoundary The event fired to navigate to the Regions
 * From Cartographic Boundary screen.
 * @param onChangeToolbarInfo A function to set information for the parent composable's toolbar.
 */
@Composable
fun RegionsAndImagesScreen(
    visibleRegionsUiState: VisibleRegionsUiState,
    visibleImagesUiState: VisibleImagesUiState,
    boundingBoxImagesUiState: BoundingBoxImagesUiState,
    selectRegionAt: (index: Int) -> Unit,
    visibleAreaChanged: (boundingBoxViewData: BoundingBoxViewData) -> Unit,
    onNavigateToRegionsFromCartographicBoundary: () -> Unit,
    onChangeToolbarInfo: (screenInfo: ScreenInfo) -> Unit
) {
    var zoomCameraToFitImages by remember { mutableStateOf(false) }

    val visibleRegions = when (visibleRegionsUiState) {
        is VisibleRegionsUiState.Loading -> emptyList()
        is VisibleRegionsUiState.VisibleRegionsLoaded -> visibleRegionsUiState.visibleRegions
    }

    val visibleImages = when (visibleImagesUiState) {
        is VisibleImagesUiState.Loading -> emptyList()
        is VisibleImagesUiState.VisibleImagesLoaded -> visibleImagesUiState.visibleImages
    }

    onChangeToolbarInfo(
        ScreenInfo(
            title = stringResource(id = R.string.regions_and_images_screen),
            actions = {
                TopAppBarActionButton(
                    iconResId = R.drawable.ic_fit_screen,
                    description = stringResource(R.string.zoom_to_fit),
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
        boundingBoxImagesUiState = boundingBoxImagesUiState,
        zoomCameraToFitImages = zoomCameraToFitImages,
        onMoveFinished = { zoomCameraToFitImages = false },
        regionPressedAt = { index ->
            selectRegionAt(index)
            onNavigateToRegionsFromCartographicBoundary()
        },
        onVisibleMapAreaChangeTo = { latLngBounds ->
            val boundingBox = latLngBounds.toBoundingBoxViewData()
            visibleAreaChanged(boundingBox)
        }
    )
}