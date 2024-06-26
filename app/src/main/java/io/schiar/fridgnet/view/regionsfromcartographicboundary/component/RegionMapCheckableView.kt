package io.schiar.fridgnet.view.regionsfromcartographicboundary.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Checkbox
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Polygon
import com.google.maps.android.compose.rememberCameraPositionState
import io.schiar.fridgnet.view.shared.util.MapUiSettingsStatic
import io.schiar.fridgnet.view.shared.util.toLatLng
import io.schiar.fridgnet.view.shared.util.toLatLngBounds
import io.schiar.fridgnet.view.shared.util.toLatLngList
import io.schiar.fridgnet.view.shared.viewdata.RegionViewData

/**
 * The component that displays a map, with the region plotted along with a checkbox on the upper
 * right.
 *
 * @param modifier the modifier used in the internal [Box].
 * @param region the region used to plot.
 * @param onRegionCheckedChangeAt the event fired when the checkbox is pressed. It carries the
 * region as a param.
 */
@Composable
fun RegionMapCheckableView(
    modifier: Modifier,
    region: RegionViewData,
    onRegionCheckedChangeAt: ((region: RegionViewData) -> Unit)? = null
) {
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(region.center.toLatLng(), 10f)
    }
    Box(modifier = modifier.fillMaxSize()) {
        GoogleMap(
            uiSettings = MapUiSettingsStatic(),
            cameraPositionState = cameraPositionState,
            onMapClick = {},
            onMapLoaded = {
                val boundingBox = region.boundingBox.toLatLngBounds()
                val cu = CameraUpdateFactory.newLatLngBounds(boundingBox, 2)
                cameraPositionState.move(cu)
            }
        ) {
            Polygon(
                points = region.polygon.geoLocations.toLatLngList(),
                holes = region.holes.map { hole -> hole.geoLocations.toLatLngList() },
                fillColor = Color.Transparent
            )
        }

        if (onRegionCheckedChangeAt != null) {
            Checkbox(
                modifier = Modifier.align(Alignment.TopEnd),
                checked = region.active,
                onCheckedChange = { onRegionCheckedChangeAt(region) }
            )
        }
    }
}