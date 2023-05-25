package io.schiar.fridgnet.view.component

import android.net.Uri
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import io.schiar.fridgnet.view.component.debug.MapPolygonInfo
import io.schiar.fridgnet.view.util.BitmapLoader
import io.schiar.fridgnet.view.util.debug.BoundsTestCreator
import io.schiar.fridgnet.view.util.debug.generatePolygonsAppCreatedUnitTest
import io.schiar.fridgnet.view.util.debug.showMapPolygonInfo
import io.schiar.fridgnet.view.util.toLatLng
import io.schiar.fridgnet.view.util.toLatLngBounds
import io.schiar.fridgnet.view.viewdata.BoundingBoxViewData
import io.schiar.fridgnet.view.viewdata.ImageViewData
import io.schiar.fridgnet.view.viewdata.RegionViewData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun Map(
    modifier: Modifier,
    visibleImages: List<ImageViewData>,
    visibleRegions: List<RegionViewData>,
    boundingBox: BoundingBoxViewData?,
    moveCamera: Boolean,
    onMoveFinished: () -> Unit,
    onClickRegion: (region: RegionViewData) -> Unit,
    onBoundsChange: (LatLngBounds?) -> Unit,
) {
    var mapLoaded by remember { mutableStateOf(value = false) }
    val bitmaps by remember { mutableStateOf(mutableMapOf<Uri, BitmapDescriptor>()) }
    val jobs = remember { mutableMapOf<Uri, Job>() }
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

    Box {
        GoogleMap(
            modifier = modifier,
            cameraPositionState = cameraPositionState,
            onMapLoaded = {
                onBoundsChange(cameraPositionState.projection?.visibleRegion?.latLngBounds)
                mapLoaded = true
            }
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

            visibleRegions.map { RegionDrawer(region = it) { region -> onClickRegion(region) } }

            visibleImages.map {
                if (!(bitmaps.containsKey(it.uri) || jobs.containsKey(it.uri))) {
                    jobs[it.uri] = coroutineScope.launch(Dispatchers.IO) {
                        bitmaps[it.uri] = withContext(Dispatchers.Default) {
                            val bitmapLoader = BitmapLoader(
                                contentResolver = context.contentResolver,
                                uri = it.uri
                            )
                            bitmapLoader.convert()
                        }
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

        if (showMapPolygonInfo) {
            MapPolygonInfo(
                modifier = Modifier.align(Alignment.TopEnd),
                visibleRegions = visibleRegions
            )
        }
    }
}