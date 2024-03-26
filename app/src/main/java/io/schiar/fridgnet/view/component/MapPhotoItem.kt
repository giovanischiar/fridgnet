package io.schiar.fridgnet.view.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import io.schiar.fridgnet.view.util.static
import io.schiar.fridgnet.view.util.toBitmapDescriptor
import io.schiar.fridgnet.view.util.toLatLng
import io.schiar.fridgnet.view.util.toLatLngBounds
import io.schiar.fridgnet.view.viewdata.BoundingBoxViewData
import io.schiar.fridgnet.view.viewdata.CartographicBoundaryViewData
import io.schiar.fridgnet.view.viewdata.ImageViewData

@Composable
fun MapPhotoItem(
    cartographicBoundary: CartographicBoundaryViewData?,
    subCartographicBoundaries: List<CartographicBoundaryViewData>,
    images: Collection<ImageViewData>,
    imagesBoundingBox: BoundingBoxViewData?,
    columnCount: Int,
    onMapClick: (LatLng) -> Unit
) {
    val cameraPositionState = rememberCameraPositionState {
        var geoLocation = cartographicBoundary?.center
        if (geoLocation == null) {
            geoLocation = images.firstOrNull()?.geoLocation
        }
        if (geoLocation != null) {
            position = CameraPosition.fromLatLngZoom(geoLocation.toLatLng(), 10f)
        }
    }
    var mapLoaded by remember { mutableStateOf(value = false) }
    Box(modifier = Modifier.size(Dp(100f * 4 / columnCount))) {
        GoogleMap(
            uiSettings = MapUiSettings().static(),
            cameraPositionState = cameraPositionState,
            onMapClick = onMapClick,
            onMapLoaded = { mapLoaded = true }
        ) {
            if (mapLoaded) {
                images.map {
                    Marker(
                        state = MarkerState(position = it.geoLocation.toLatLng()),
                        icon = it.byteArray.toBitmapDescriptor()
                    )
                }
                if (
                    cartographicBoundary == null &&
                    subCartographicBoundaries.isEmpty() &&
                    imagesBoundingBox != null
                ) {
                    val boundingBox = imagesBoundingBox.toLatLngBounds()
                    val cu = CameraUpdateFactory.newLatLngBounds(boundingBox, 2)
                    cameraPositionState.move(cu)
                }

                if (cartographicBoundary != null) {
                    CartographicBoundaryDrawer(cartographicBoundary = cartographicBoundary)
                    val boundingBox = cartographicBoundary.boundingBox.toLatLngBounds()
                    val cu = CameraUpdateFactory.newLatLngBounds(boundingBox, 2)
                    cameraPositionState.move(cu)
                }

                subCartographicBoundaries.forEach {
                    val subCartographicBoundary = it
                    CartographicBoundaryDrawer(cartographicBoundary = subCartographicBoundary)
                    if (subCartographicBoundaries.size == 1 && cartographicBoundary == null) {
                        val boundingBox = subCartographicBoundary.boundingBox.toLatLngBounds()
                        val cu = CameraUpdateFactory.newLatLngBounds(boundingBox, 2)
                        cameraPositionState.move(cu)
                    }
                }
            }
        }
        if (images.size > 1) {
            Text(modifier=Modifier.align(Alignment.TopEnd), text="${images.size}")
        }

        Button(
            modifier = Modifier.fillMaxSize(),
            onClick = { onMapClick(LatLng(0.0, 0.0)) },
            contentPadding = PaddingValues(all = 0.dp),
            shape = RectangleShape,
            colors = ButtonColors(
                containerColor = Color.Transparent,
                contentColor = Color.Transparent,
                disabledContainerColor = Color.Transparent,
                disabledContentColor = Color.Transparent
            )
        ) {}
    }
}