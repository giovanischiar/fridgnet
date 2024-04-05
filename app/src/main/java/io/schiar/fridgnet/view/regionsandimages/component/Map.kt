package io.schiar.fridgnet.view.regionsandimages.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.android.gms.maps.model.LatLngBounds
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.rememberCameraPositionState
import io.schiar.fridgnet.view.shared.component.RegionDrawer
import io.schiar.fridgnet.view.shared.component.ZoomControls
import io.schiar.fridgnet.view.shared.component.debug.MapPolygonInfo
import io.schiar.fridgnet.view.shared.util.debug.BoundsTestCreator
import io.schiar.fridgnet.view.shared.util.debug.generatePolygonsAppCreatedUnitTest
import io.schiar.fridgnet.view.shared.util.debug.showMapPolygonInfo
import io.schiar.fridgnet.view.shared.util.updateCameraPositionTo
import io.schiar.fridgnet.view.shared.viewdata.BoundingBoxViewData
import io.schiar.fridgnet.view.shared.viewdata.ImageViewData
import io.schiar.fridgnet.view.shared.viewdata.RegionViewData

@Composable
fun Map(
    modifier: Modifier,
    visibleImages: List<ImageViewData>,
    visibleRegions: List<RegionViewData>,
    boundingBox: BoundingBoxViewData?,
    zoomCameraToFitImages: Boolean,
    onMoveFinished: () -> Unit,
    regionPressedAt: (index: Int) -> Unit,
    onVisibleMapRegionChangeTo: (LatLngBounds) -> Unit,
) {
    var isMapLoaded by remember { mutableStateOf(value = false) }
    val coroutineScope = rememberCoroutineScope()
    val cameraPositionState = rememberCameraPositionState()

    fun handleVisibleMapRegionChange() {
        if (cameraPositionState.isMoving) return
        val projection = cameraPositionState.projection
        if (projection != null) {
            val visibleRegion = projection.visibleRegion
            val latLngBounds = visibleRegion.latLngBounds
            onVisibleMapRegionChangeTo(latLngBounds)
        }
    }

    fun zoomToFitImages() {
        cameraPositionState.updateCameraPositionTo(
            boundingBox = boundingBox,
            coroutineScope = coroutineScope,
            animate = true,
            onMoveFinished = onMoveFinished
        )
    }

    LaunchedEffect(boundingBox) { zoomToFitImages() }

    LaunchedEffect(cameraPositionState.isMoving) { handleVisibleMapRegionChange() }

    if (zoomCameraToFitImages && isMapLoaded) { zoomToFitImages() }

    Box(modifier = modifier.fillMaxSize()) {
        GoogleMap(
            cameraPositionState = cameraPositionState,
            onMapLoaded = {
                handleVisibleMapRegionChange()
                isMapLoaded = true
            },
            uiSettings = MapUiSettings(zoomControlsEnabled = false),
        ) {
            visibleRegions.mapIndexed { index, region ->
                RegionDrawer(region = region, onPressed = { regionPressedAt(index) } )
            }

            ImagesDrawer(images = visibleImages)
        }

        ZoomControls(
            modifier = Modifier.align(Alignment.BottomEnd).padding(end = 15.dp, bottom = 85.dp),
            cameraPositionState = cameraPositionState,
            enabled = isMapLoaded
        )

        if (showMapPolygonInfo) {
            MapPolygonInfo(
                modifier = Modifier.align(Alignment.TopEnd),
                visibleRegions = visibleRegions
            )
        }
    }

    if (generatePolygonsAppCreatedUnitTest) {
        BoundsTestCreator().generateTest(
            cameraPositionState = cameraPositionState,
            visibleRegions = visibleRegions
        )
    }
}