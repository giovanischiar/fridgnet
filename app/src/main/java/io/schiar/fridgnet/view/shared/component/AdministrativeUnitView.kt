package io.schiar.fridgnet.view.shared.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.unit.dp
import com.google.android.gms.maps.model.CameraPosition
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import io.schiar.fridgnet.view.shared.util.MapUiSettingsStatic
import io.schiar.fridgnet.view.shared.util.toBitmapDescriptor
import io.schiar.fridgnet.view.shared.util.toLatLng
import io.schiar.fridgnet.view.shared.util.toLatLngBounds
import io.schiar.fridgnet.view.shared.util.updateCameraPositionTo
import io.schiar.fridgnet.view.shared.viewdata.AdministrativeUnitViewData

/**
 * The Component that visually shows the Administrative Unit.
 *
 * @param modifier the modifier used for the internal [Box].
 * @param administrativeUnit the administrative unit used.
 * @param mapUISettings the data for the Google Maps component used.
 * @param areZoomControlsEnabled whether the zoom controls is showed in the component.
 * @param areImagesShowing whether the images are shown within the map.
 * @param areImagesSizeShowing whether the image size info are shown within the map.
 * @param areSubDivisionsShowing whether the cartographic boundary's subdivisions are shown within the
 * map.
 * @param onPress fired when the Administrative Unit is pressed.
 */
@Composable
fun AdministrativeUnitView(
    modifier: Modifier = Modifier,
    administrativeUnit: AdministrativeUnitViewData,
    mapUISettings: MapUiSettings = MapUiSettingsStatic(),
    areZoomControlsEnabled: Boolean = false,
    areImagesShowing: Boolean = true,
    areImagesSizeShowing: Boolean = true,
    areSubDivisionsShowing: Boolean = true,
    onPress: (() -> Unit)? = null
) {
    val (
        _, _,
        cartographicBoundary,
        subCartographicBoundaries,
        images,
        imagesBoundingBox
    ) = administrativeUnit

    val boundingBoxToMove = cartographicBoundary?.boundingBox
        ?: if (subCartographicBoundaries.isNotEmpty()) {
            subCartographicBoundaries[0].boundingBox
        } else {
            imagesBoundingBox
        }

    val coroutineScope = rememberCoroutineScope()
    val cameraPositionState: CameraPositionState = rememberCameraPositionState {
        if (boundingBoxToMove == null) return@rememberCameraPositionState
        val firstLatLngOfMapToMove = boundingBoxToMove.toLatLngBounds().center
        position = CameraPosition.fromLatLngZoom(firstLatLngOfMapToMove, 10f)
    }
    var mapLoaded by remember { mutableStateOf(value = false) }
    if (mapLoaded) cameraPositionState.updateCameraPositionTo(boundingBoxToMove, coroutineScope)
    Box(modifier = modifier) {
        GoogleMap(
            uiSettings = mapUISettings,
            cameraPositionState = cameraPositionState,
            onMapLoaded = { mapLoaded = true }
        ) {
            if (!mapLoaded) return@GoogleMap

            if (areImagesShowing) {
                images.map { image ->
                    val position = image.geoLocation.toLatLng()
                    val icon = image.byteArray.toBitmapDescriptor()
                    Marker(state = MarkerState(position), icon = icon)
                }
            }

            if (cartographicBoundary != null) {
                CartographicBoundaryDrawer(cartographicBoundary)
            }

            if (areSubDivisionsShowing) {
                subCartographicBoundaries.forEach { subCartographicBoundary ->
                    CartographicBoundaryDrawer(cartographicBoundary = subCartographicBoundary)
                }
            }
        }

        if (images.size > 1 && areImagesSizeShowing) {
            Text(modifier = Modifier.align(Alignment.TopEnd), text = "${images.size}")
        }

        if (areZoomControlsEnabled) {
            ZoomControls(
                modifier = Modifier.align(Alignment.BottomEnd).padding(bottom = 5.dp, end = 5.dp),
                cameraPositionState = cameraPositionState,
                boundingBox = cartographicBoundary?.boundingBox,
                imagesBoundingBox = imagesBoundingBox,
                enabled = mapLoaded
            )
        }

        if (onPress != null) {
            Button(
                modifier = Modifier.fillMaxSize(),
                onClick = onPress,
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
}