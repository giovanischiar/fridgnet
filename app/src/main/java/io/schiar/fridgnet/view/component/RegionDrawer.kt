package io.schiar.fridgnet.view.component

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.google.maps.android.compose.Polygon
import io.schiar.fridgnet.view.component.debug.BoundingBoxDrawer
import io.schiar.fridgnet.view.util.debug.showBoundingBoxPolygon
import io.schiar.fridgnet.view.util.toLatLngList
import io.schiar.fridgnet.view.viewdata.RegionViewData

@Composable
fun RegionDrawer(region: RegionViewData, onClick: (region: RegionViewData) -> Unit = {}) {
    Polygon(
        points = region.polygon.coordinates.toLatLngList(),
        onClick = { onClick(region) },
        visible = region.active,
        holes = region.holes.map { hole -> hole.coordinates.toLatLngList() },
        fillColor = Color.Transparent,
        clickable = true,
        zIndex = region.zIndex
    )

    if (showBoundingBoxPolygon) {
        BoundingBoxDrawer(boundingBox = region.boundingBox)
    }
}