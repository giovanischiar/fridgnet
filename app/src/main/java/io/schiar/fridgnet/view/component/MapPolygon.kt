package io.schiar.fridgnet.view.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Checkbox
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Polygon
import com.google.maps.android.compose.rememberCameraPositionState
import io.schiar.fridgnet.view.util.static
import io.schiar.fridgnet.view.util.toLatLng
import io.schiar.fridgnet.view.util.toLatLngBounds
import io.schiar.fridgnet.view.util.toLatLngList
import io.schiar.fridgnet.view.viewdata.RegionViewData

@Composable
fun MapPolygon(
    modifier: Modifier,
    region: RegionViewData,
    onRegionCheckedChangeAt: ((region: RegionViewData) -> Unit)? = null
) {
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(region.center.toLatLng(), 10f)
    }
    Box(modifier = modifier.fillMaxSize()) {
        GoogleMap(
            uiSettings = MapUiSettings().static(),
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

        if (onRegionCheckedChangeAt != null) {
            Checkbox(
                modifier = Modifier.align(Alignment.TopEnd),
                checked = region.active,
                onCheckedChange = { onRegionCheckedChangeAt(region) }
            )
        }
    }
}