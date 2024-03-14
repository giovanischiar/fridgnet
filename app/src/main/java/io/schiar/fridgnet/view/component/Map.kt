package io.schiar.fridgnet.view.component

import android.net.Uri
import androidx.compose.foundation.layout.Box
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import io.schiar.fridgnet.Log
import io.schiar.fridgnet.view.component.debug.MapPolygonInfo
import io.schiar.fridgnet.view.util.debug.BoundsTestCreator
import io.schiar.fridgnet.view.util.debug.generatePolygonsAppCreatedUnitTest
import io.schiar.fridgnet.view.util.debug.showMapPolygonInfo
import io.schiar.fridgnet.view.util.toBitmapDescriptor
import io.schiar.fridgnet.view.util.toLatLng
import io.schiar.fridgnet.view.util.toLatLngBounds
import io.schiar.fridgnet.view.viewdata.BoundingBoxViewData
import io.schiar.fridgnet.view.viewdata.ImageViewData
import io.schiar.fridgnet.view.viewdata.RegionViewData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.time.measureTime
import java.util.Collections.synchronizedMap as syncMapOf

@Composable
fun Map(
    modifier: Modifier,
    visibleImages: List<ImageViewData>,
    visibleRegions: List<RegionViewData>,
    boundingBox: BoundingBoxViewData?,
    moveCamera: Boolean,
    onMoveFinished: () -> Unit,
    regionPressedAt: (index: Int) -> Unit,
    onBoundsChange: (LatLngBounds?) -> Unit,
) {
    var mapLoaded by remember { mutableStateOf(value = false) }
    val bitmaps by remember { mutableStateOf(syncMapOf(mutableMapOf<Uri, BitmapDescriptor>())) }
    val jobs = remember { syncMapOf(mutableMapOf<Uri, Job>()) }
    val coroutineScope = rememberCoroutineScope()

    val context = LocalContext.current
    val missionDoloresPark = LatLng(37.759773, -122.427063)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(missionDoloresPark, 10f)
    }

    if (generatePolygonsAppCreatedUnitTest) {
        BoundsTestCreator().generateTest(
            cameraPositionState = cameraPositionState,
            visibleRegions = visibleRegions
        )
    }

    LaunchedEffect(cameraPositionState.isMoving) {
        if (!cameraPositionState.isMoving) {
            onBoundsChange(cameraPositionState.projection?.visibleRegion?.latLngBounds)
        }
    }

    Box(modifier = modifier) {
        GoogleMap(
            cameraPositionState = cameraPositionState,
            onMapLoaded = {
                onBoundsChange(cameraPositionState.projection?.visibleRegion?.latLngBounds)
                mapLoaded = true
            },
            uiSettings = MapUiSettings(zoomControlsEnabled = false)
        ) {
            if (moveCamera && boundingBox != null && mapLoaded) {
                val cu = CameraUpdateFactory.newLatLngBounds(boundingBox.toLatLngBounds(), 2)
                coroutineScope.launch(Dispatchers.Main) {
                    withContext(coroutineContext) {
                        cameraPositionState.animate(cu, 1000)
                    }
                    onMoveFinished()
                }
            }

            val time = measureTime {
                visibleRegions.mapIndexed { index, region ->
                    RegionDrawer(region = region, index = index, onClick = regionPressedAt )
                }
            }
            
            Log.d("", "it took $time to draw all of the ${visibleRegions.size} regions")

            visibleImages.map {
                if (!(bitmaps.containsKey(it.uri) || jobs.containsKey(it.uri))) {
                    jobs[it.uri] = coroutineScope.launch(Dispatchers.IO) {
                        val bitmap = withContext(Dispatchers.IO) {
                            it.byteArray.toBitmapDescriptor()
                        }
                        bitmaps[it.uri] = bitmap
                        jobs.remove(it.uri)
                    }
                }
                Marker(
                    state = MarkerState(position = it.coordinate.toLatLng()),
                    icon = bitmaps[it.uri],
                    visible = bitmaps.containsKey(it.uri)
                )
            }
        }

        ZoomControls(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 15.dp, bottom = 85.dp),
            cameraPositionState = cameraPositionState,
            enabled = mapLoaded
        )

        if (showMapPolygonInfo) {
            MapPolygonInfo(
                modifier = Modifier.align(Alignment.TopEnd),
                visibleRegions = visibleRegions
            )
        }
    }
}