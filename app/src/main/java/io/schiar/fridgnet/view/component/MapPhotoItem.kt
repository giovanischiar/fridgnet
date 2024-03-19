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
import io.schiar.fridgnet.view.viewdata.CartographicBoundaryViewData
import io.schiar.fridgnet.view.viewdata.GeoLocationViewData

@Composable
fun MapPhotoItem(
    initialGeoLocation: GeoLocationViewData?,
    cartographicBoundary: CartographicBoundaryViewData?,
    columnCount: Int,
    onMapClick: (LatLng) -> Unit
) {
    val cameraPositionState = rememberCameraPositionState {
        var geoLocation = cartographicBoundary?.center
        if (geoLocation == null) {
            geoLocation = initialGeoLocation
        }
        if (geoLocation != null) {
            position = CameraPosition.fromLatLngZoom(geoLocation.toLatLng(), 10f)
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
        if (cartographicBoundary != null && mapLoaded) {
            CartographicBoundaryDrawer(cartographicBoundary = cartographicBoundary)
            val boundingBox = cartographicBoundary.boundingBox.toLatLngBounds()
            val cu = CameraUpdateFactory.newLatLngBounds(boundingBox, 2)
            cameraPositionState.move(cu)
        }
    }
}