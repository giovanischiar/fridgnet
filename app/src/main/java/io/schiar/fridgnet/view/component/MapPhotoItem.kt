package io.schiar.fridgnet.view.component

import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
    initialCoordinate: CoordinateViewData?,
    location: LocationViewData?,
    columnCount: Int,
    onMapClick: (LatLng) -> Unit
) {
    val cameraPositionState = rememberCameraPositionState {
        var coordinate = location?.center
        if (coordinate == null) {
            coordinate = initialCoordinate
        }
        if (coordinate != null) {
            position = CameraPosition.fromLatLngZoom(coordinate.toLatLng(), 10f)
        }
    }
    var mapLoaded by remember { mutableStateOf(value = false) }

    GoogleMap(
        modifier = Modifier.size(Dp(100f * 4 / columnCount)),
        uiSettings = MapUiSettings().static(),
        cameraPositionState = cameraPositionState,
        onMapClick = onMapClick,
        onMapLoaded = { mapLoaded = true }
    ) {
        if (location != null && mapLoaded) {
            LocationDrawer(location = location)
            val boundingBox = location.boundingBox.toLatLngBounds()
            val cu = CameraUpdateFactory.newLatLngBounds(boundingBox, 2)
            cameraPositionState.move(cu)
        }
    }
}