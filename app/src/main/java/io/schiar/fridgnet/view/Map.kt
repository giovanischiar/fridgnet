package io.schiar.fridgnet.view

import android.net.Uri
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import io.schiar.fridgnet.view.viewdata.ImageViewData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun Map(
    modifier: Modifier,
    visibleImages: List<ImageViewData>,
    onBoundsChange: (LatLngBounds?) -> Unit
) {
    val bitmaps by remember { mutableStateOf(mutableMapOf<Uri, BitmapDescriptor>()) }
    val jobs = remember { mutableMapOf<Uri, Job>() }
    val coroutineScope = rememberCoroutineScope()

    val context = LocalContext.current
    val missionDoloresPark = LatLng(37.759773, -122.427063)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(missionDoloresPark, 10f)
    }

    LaunchedEffect(cameraPositionState.isMoving) {
        if (!cameraPositionState.isMoving) {
            onBoundsChange(cameraPositionState.projection?.visibleRegion?.latLngBounds)
        }
    }

    GoogleMap(
        modifier = modifier,
        cameraPositionState = cameraPositionState,
        onMapLoaded = {
            onBoundsChange(cameraPositionState.projection?.visibleRegion?.latLngBounds)
        }
    ) {
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
            val position = LatLng(it.location.lat.toDouble(), it.location.lng.toDouble())
            Marker(
                state = MarkerState(position = position),
                icon = bitmaps[it.uri],
                visible = bitmaps.containsKey(it.uri)
            )
        }
    }
}