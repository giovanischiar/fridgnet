package io.schiar.fridgnet.view.screen

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Polygon
import com.google.maps.android.compose.rememberCameraPositionState
import io.schiar.fridgnet.view.util.toLatLngBounds
import io.schiar.fridgnet.view.util.toLatLngList
import io.schiar.fridgnet.viewmodel.MainViewModel

@Composable
fun PolygonsScreen(viewModel: MainViewModel) {
    val location by viewModel.currentLocation.collectAsState()
    val sortedRegions = (location ?: return).regions.sortedBy { it.polygon.coordinates.size }.asReversed()
    LazyColumn {
        sortedRegions.map { region ->
            item {
                val missionDoloresPark = LatLng(37.759773, -122.427063)
                val cameraPositionState = rememberCameraPositionState {
                    position = CameraPosition.fromLatLngZoom(missionDoloresPark, 10f)
                }
                GoogleMap(
                    modifier = Modifier.fillMaxWidth().height(Dp(300f)),
                    uiSettings = MapUiSettings(
                        compassEnabled = false,
                        zoomControlsEnabled = false,
                        zoomGesturesEnabled = false,
                        tiltGesturesEnabled = false,
                        indoorLevelPickerEnabled = false,
                        rotationGesturesEnabled = false
                    ),
                    cameraPositionState = cameraPositionState,
                    onMapClick = {},
                    onMapLoaded = {
                        val boundingBox = region.boundingBox.toLatLngBounds()
                        val cu = CameraUpdateFactory.newLatLngBounds(boundingBox, 2)
                        cameraPositionState.move(cu)
                    }
                ) {
                    Polygon(
                        points = region.polygon.coordinates.toLatLngList(),
                        holes = region.holes.map { hole -> hole.coordinates.toLatLngList() },
                        fillColor = Color.Transparent
                    )
                }
            }
        }
    }
}