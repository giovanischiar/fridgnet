package io.schiar.fridgnet.view.screen

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

@Composable
fun Map(
    modifier: Modifier,
    visibleImages: List<ImageViewData>,
    onBoundsChange: (LatLngBounds?) -> Unit
) {
    val alreadyExistedBitmaps = remember { mutableMapOf<Uri, BitmapDescriptor>() }
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
            val icon: BitmapDescriptor = if (alreadyExistedBitmaps.contains(it.uri)) {
                alreadyExistedBitmaps[it.uri] ?: return@map
            } else {
                val rawBitmap = if (Build.VERSION.SDK_INT < 28) {
                    @Suppress("DEPRECATION")
                    MediaStore.Images.Media.getBitmap(context.contentResolver, it.uri)
                } else {
                    val source = ImageDecoder.createSource(context.contentResolver, it.uri)
                    ImageDecoder.decodeBitmap(source)
                }
                val bitmap = Bitmap.createScaledBitmap(
                    rawBitmap,
                    150,
                    150,
                    false
                )
                BitmapDescriptorFactory.fromBitmap(bitmap)
            }
            val position = LatLng(it.location.lat.toDouble(), it.location.lng.toDouble())
            Marker(
                state = MarkerState(position = position),
                icon = icon
            )
            alreadyExistedBitmaps[it.uri] = icon
        }
    }
}