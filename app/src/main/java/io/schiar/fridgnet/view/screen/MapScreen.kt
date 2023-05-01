package io.schiar.fridgnet.view.screen

import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.google.android.gms.maps.model.*
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import io.schiar.fridgnet.view.PhotoPicker
import io.schiar.fridgnet.view.viewdata.ImageViewData
import io.schiar.fridgnet.viewmodel.MainViewModel
import kotlinx.coroutines.*

@Composable
fun MapScreen(viewModel: MainViewModel) {
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        val (photoPickerShowing, isPhotoPickerShowing) = remember { mutableStateOf(false) }
        val visibleImages by viewModel.visibleImages.collectAsState()

        Map(
            modifier = Modifier.fillMaxSize(),
            visibleImages = visibleImages
        ) { viewModel.visibleAreaChanged(it) }

        Button(
            onClick = { isPhotoPickerShowing.invoke(true) },
            modifier = Modifier.align(Alignment.BottomCenter)
        ) {
            Text("Add Photos")
        }

        if (photoPickerShowing) {
            PhotoPicker { uri, date, latitude, longitude ->
                viewModel.addImage(
                    uri = uri,
                    date = date,
                    latitude = latitude,
                    longitude = longitude
                )
                isPhotoPickerShowing.invoke(false)
            }
        }
    }
}
fun loadBitmap(uri: Uri, context: Context): BitmapDescriptor {
    val rawBitmap = if (Build.VERSION.SDK_INT < 28) {
        @Suppress("DEPRECATION")
        MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
    } else {
        val source = ImageDecoder.createSource(context.contentResolver, uri)
        ImageDecoder.decodeBitmap(source)
    }
    val bitmap = Bitmap.createScaledBitmap(rawBitmap, 150, 150, false)
    return BitmapDescriptorFactory.fromBitmap(bitmap)
}

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
                        loadBitmap(it.uri, context)
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