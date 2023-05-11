package io.schiar.fridgnet.view.component

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.google.maps.android.compose.Polygon
import io.schiar.fridgnet.view.util.toLatLngList
import io.schiar.fridgnet.view.viewdata.LocationViewData

@Composable
fun LocationDrawer(location: LocationViewData, onClickLocation: () -> Unit = {}) {
    location.regions.map {
        Polygon(
            points = it.polygon.coordinates.toLatLngList(),
            onClick = {
                onClickLocation()
            },
            visible = it.active,
            holes = it.holes.map { hole -> hole.coordinates.toLatLngList() },
            fillColor = Color.Transparent,
            clickable = true
        )
    }
}