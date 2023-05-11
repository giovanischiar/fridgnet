package io.schiar.fridgnet.view.component

import android.net.Uri
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.maps.android.compose.*
import io.schiar.fridgnet.view.util.BitmapLoader
import io.schiar.fridgnet.view.util.toLatLng
import io.schiar.fridgnet.view.util.toLatLngList
import io.schiar.fridgnet.view.viewdata.ImageViewData
import io.schiar.fridgnet.view.viewdata.LocationViewData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun Map(
    modifier: Modifier,
    visibleImages: List<ImageViewData>,
    countries: Map<String, LocationViewData>,
    states: Map<String, Map<String, LocationViewData>>,
    counties: Map<String, Map<String, LocationViewData>>,
    cities: Map<String, Map<String, LocationViewData>>,
    onBoundsChange: (LatLngBounds?) -> Unit,
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
//        if (states.values.isNotEmpty() && states.values.toList()[0].values.isNotEmpty()) {
//            val boundingBox = states.values.toList()[0].values.toList()[0].boundingBox
//            val cu = CameraUpdateFactory.newLatLngBounds(boundingBox, 2)
//            cameraPositionState.move(cu)
//        }

        countries.values.map { location -> LocationDrawer(location) }
        states.values.map { stringLocation -> stringLocation.values.map { location ->
            LocationDrawer(location)
        } }

        counties.values.map { stringLocation -> stringLocation.values.map { location ->
            LocationDrawer(location)
        } }

        cities.values.map { stringLocation -> stringLocation.values.map { location ->
            LocationDrawer(location)
        } }

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
}