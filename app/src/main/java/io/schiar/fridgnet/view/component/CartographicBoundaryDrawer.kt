package io.schiar.fridgnet.view.component

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.google.maps.android.compose.Polygon
import io.schiar.fridgnet.view.util.toLatLngList
import io.schiar.fridgnet.view.viewdata.CartographicBoundaryViewData

@Composable
fun CartographicBoundaryDrawer(cartographicBoundary: CartographicBoundaryViewData) {
    cartographicBoundary.regions.map {
        Polygon(
            points = it.polygon.geoLocations.toLatLngList(),
            visible = it.active,
            holes = it.holes.map { hole -> hole.geoLocations.toLatLngList() },
            fillColor = Color.Transparent
        )
    }
}