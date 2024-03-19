package io.schiar.fridgnet.view.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Icon
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import io.schiar.fridgnet.R
import io.schiar.fridgnet.view.component.CartographicBoundaryDrawer
import io.schiar.fridgnet.view.component.PhotoGrid
import io.schiar.fridgnet.view.component.ZoomControls
import io.schiar.fridgnet.view.util.ScreenInfo
import io.schiar.fridgnet.view.util.toBitmapDescriptor
import io.schiar.fridgnet.view.util.toLatLng
import io.schiar.fridgnet.view.util.toLatLngBounds
import io.schiar.fridgnet.view.viewdata.BoundingBoxViewData
import io.schiar.fridgnet.viewmodel.PhotosViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun PhotosScreen(
    viewModel: PhotosViewModel,
    info: (screenInfo: ScreenInfo) -> Unit
) {
    var mapLoaded by remember { mutableStateOf(false) }
    var zoomedOut by remember { mutableStateOf(true) }

    val cartographicBoundaryImages by viewModel.cartographicBoundaryImages.collectAsState(
        initial = null
    )
    val coroutineScope = rememberCoroutineScope()

    val (cartographicBoundary, images, imagesBondingBox) = cartographicBoundaryImages ?: return

    val missionDoloresPark = LatLng(37.759773, -122.427063)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(missionDoloresPark, 10f)
    }

    val weight = remember { 0.65f }

    fun moveCamera(boundingBox: BoundingBoxViewData, animate: Boolean = false, padding: Int = 2) {
        if (mapLoaded) {
            val latLngBounds = boundingBox.toLatLngBounds()
            val cu = CameraUpdateFactory.newLatLngBounds(latLngBounds, padding)
            if (animate) {
                coroutineScope.launch(Dispatchers.Main) {
                    cameraPositionState.animate(cu, 1000)
                }
                return
            }
            cameraPositionState.move(cu)
        }
    }

    info(
        ScreenInfo(title = cartographicBoundary.administrativeUnit)
    )
    Column {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(weight)
        ) {
            GoogleMap(
                cameraPositionState = cameraPositionState,
                properties = MapProperties(
                    latLngBoundsForCameraTarget = cartographicBoundary.boundingBox.toLatLngBounds()
                ),
                uiSettings = MapUiSettings(zoomControlsEnabled = false),
                onMapLoaded = {
                    mapLoaded = true
                    moveCamera(boundingBox = cartographicBoundary.boundingBox)
                }
            ) {
                CartographicBoundaryDrawer(cartographicBoundary = cartographicBoundary)
                images.map {
                    Marker(
                        state = MarkerState(position = it.geoLocation.toLatLng()),
                        icon = it.byteArray.toBitmapDescriptor()
                    )
                }
            }
            Column(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(bottom = 5.dp, end = 5.dp),
                verticalArrangement = Arrangement.spacedBy(15.dp)
            ) {
                FloatingActionButton(
                    containerColor = colorResource(id = R.color.indigo_dye_500).copy(alpha = 0.40f),
                    elevation = FloatingActionButtonDefaults.elevation(0.dp),
                    onClick = {
                        val imagesBoundingBox =
                            imagesBondingBox ?: return@FloatingActionButton
                        val boundingBox = (if (zoomedOut) {
                            imagesBoundingBox
                        } else {
                            cartographicBoundary.boundingBox
                        })
                        moveCamera(boundingBox = boundingBox, animate = true, padding = 27)
                        zoomedOut = !zoomedOut
                    }
                ) {
                    Icon(
                        painter = painterResource(
                            id = if (zoomedOut) {
                                R.drawable.ic_zoom_in_map
                            } else {
                                R.drawable.ic_fit_screen
                            }
                        ),
                        contentDescription = if (zoomedOut) {
                            "Zoom to fit all photos"
                        } else {
                            "Zoom to fit the whole cartographicBoundary"
                        },
                        tint = colorResource(id = R.color.white).copy(alpha = 0.40f)
                    )
                }
                ZoomControls(cameraPositionState = cameraPositionState, enabled = mapLoaded)
            }
        }
        PhotoGrid(modifier = Modifier.weight(1 - weight), images = images)
    }
}