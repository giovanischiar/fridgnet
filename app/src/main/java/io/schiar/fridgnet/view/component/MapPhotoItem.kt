package io.schiar.fridgnet.view.component

import androidx.compose.foundation.layout.size
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.rememberCameraPositionState
import io.schiar.fridgnet.view.util.static
import io.schiar.fridgnet.view.util.toLatLng
import io.schiar.fridgnet.view.util.toLatLngBounds
import io.schiar.fridgnet.view.viewdata.CoordinateViewData
import io.schiar.fridgnet.view.viewdata.LocationViewData

@Composable
fun MapPhotoItem(
    initialLocation: CoordinateViewData,
    location: LocationViewData?,
    onMapClick: (latLng: LatLng) -> Unit
) {
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(initialLocation.toLatLng(), 10f)
    }
    var mapLoaded by remember { mutableStateOf(value = false) }

    GoogleMap(
        modifier = Modifier.size(Dp(100f)),
        uiSettings = MapUiSettings().static(),
        cameraPositionState = cameraPositionState,
        onMapClick = onMapClick,
        onMapLoaded = { mapLoaded = true }
    ) {
        if (location != null && mapLoaded) {
            LocationDrawer(location = location) { onMapClick(location.center.toLatLng()) }
            val boundingBox = location.boundingBox.toLatLngBounds()
            val cu = CameraUpdateFactory.newLatLngBounds(boundingBox, 2)
            cameraPositionState.move(cu)
        }
    }
}