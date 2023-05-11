package io.schiar.fridgnet.view.component

import androidx.compose.runtime.Composable
import com.google.maps.android.compose.Polyline
import io.schiar.fridgnet.view.util.toLatLngList
import io.schiar.fridgnet.view.viewdata.LocationViewData

@Composable
fun LocationDrawer(location: LocationViewData) {
    location.regions.map {
        if (it.active) {
            Polyline(points = it.polygon.coordinates.toLatLngList())
            it.holes.map { hole ->
                Polyline(points = hole.coordinates.toLatLngList())
            }
        }
    }
}