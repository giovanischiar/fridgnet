package io.schiar.fridgnet.view.shared.component

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.google.maps.android.compose.Polygon
import io.schiar.fridgnet.view.shared.util.toLatLngList
import io.schiar.fridgnet.view.shared.viewdata.CartographicBoundaryViewData

/**
 * The component used to draw the Cartographic Boundary inside a Google Maps Component. It creates
 * a Polygon component for each region.
 *
 * @param cartographicBoundary the data from Cartographic Boundary.
 */
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