package io.schiar.fridgnet.view.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import io.schiar.fridgnet.view.component.LocationDrawer
import io.schiar.fridgnet.view.component.PhotoGrid
import io.schiar.fridgnet.view.util.ScreenInfo
import io.schiar.fridgnet.view.util.toBitmapDescriptor
import io.schiar.fridgnet.view.util.toLatLng
import io.schiar.fridgnet.view.util.toLatLngBounds
import io.schiar.fridgnet.viewmodel.PhotosViewModel

@Composable
fun PhotosScreen(
    viewModel: PhotosViewModel,
    info: (screenInfo: ScreenInfo) -> Unit
) {
    val selectedImages by viewModel.selectedImages.collectAsState()
    val selectedLocation by viewModel.selectedLocation.collectAsState()
    val selectedBoundingBox by viewModel.selectedBoundingBox.collectAsState()

    val (title, images) = selectedImages ?: return

    val missionDoloresPark = LatLng(37.759773, -122.427063)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(missionDoloresPark, 10f)
    }

    val weight = remember { 0.65f }

    LaunchedEffect(Unit) { viewModel.subscribe() }

    info(
        ScreenInfo(title = title)
    )
    Column {
        GoogleMap(
            modifier = Modifier
                .fillMaxWidth()
                .weight(weight),
            cameraPositionState = cameraPositionState,
            properties = MapProperties(
                latLngBoundsForCameraTarget = selectedBoundingBox?.toLatLngBounds()
            ),
            onMapLoaded = {
                selectedLocation?.let { location ->
                    val boundingBox = location.boundingBox.toLatLngBounds()
                    val cu = CameraUpdateFactory.newLatLngBounds(boundingBox, 2)
                    cameraPositionState.move(cu)
                }
            }
        ) {
            selectedLocation?.let { LocationDrawer(location = it) }
            images.map {
                Marker(
                    state = MarkerState(position = it.coordinate.toLatLng()),
                    icon = it.byteArray.toBitmapDescriptor()
                )
            }
        }
        PhotoGrid(modifier = Modifier.weight(1 - weight), images = images)
    }
}