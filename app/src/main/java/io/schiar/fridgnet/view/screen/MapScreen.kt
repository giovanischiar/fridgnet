package io.schiar.fridgnet.view.screen

import android.graphics.Bitmap
import android.graphics.ImageDecoder
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
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import io.schiar.fridgnet.view.PhotoPicker
import io.schiar.fridgnet.viewmodel.MainViewModel

@Composable
fun MapScreen(viewModel: MainViewModel) {
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        val context = LocalContext.current
        val missionDoloresPark = LatLng(37.759773, -122.427063)
        val cameraPositionState = rememberCameraPositionState {
            position = CameraPosition.fromLatLngZoom(missionDoloresPark, 10f)
        }
        val (photoPickerShowing, isPhotoPickerShowing) = remember { mutableStateOf(false) }
        val images by viewModel.images.collectAsState()

        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState
        ) {
            images.map {
                val rawBitmap = if (Build.VERSION.SDK_INT < 28) {
                    @Suppress("DEPRECATION")
                    MediaStore.Images.Media.getBitmap(context.contentResolver, it.uri)
                } else {
                    val source = ImageDecoder.createSource(context.contentResolver, it.uri)
                    ImageDecoder.decodeBitmap(source)
                }
                val bitmap = Bitmap.createScaledBitmap(rawBitmap, 150, 150, false)
                val icon = BitmapDescriptorFactory.fromBitmap(bitmap)
                val position = LatLng(it.location.lat.toDouble(), it.location.lng.toDouble())
                Marker(
                    state = MarkerState(position = position),
                    icon = icon
                )
            }
        }

        Button(
            onClick = { isPhotoPickerShowing.invoke(true) },
            modifier = Modifier.align(Alignment.BottomCenter)
        ) {
            Text("Add Photos")
        }

        if (photoPickerShowing) {
            PhotoPicker { uri, date, latitude, longitude ->
                viewModel.addImage(uri = uri, date = date, latitude = latitude, longitude = longitude)
                isPhotoPickerShowing.invoke(false)
            }
        }
    }
}