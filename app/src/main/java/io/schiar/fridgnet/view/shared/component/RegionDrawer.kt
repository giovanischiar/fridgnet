package io.schiar.fridgnet.view.shared.component

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.google.maps.android.compose.Polygon
import io.schiar.fridgnet.view.shared.component.debug.BoundingBoxDrawer
import io.schiar.fridgnet.view.shared.util.debug.showBoundingBoxPolygon
import io.schiar.fridgnet.view.shared.util.toLatLngList
import io.schiar.fridgnet.view.shared.viewdata.RegionViewData

/**
 * The component used to draw the Region inside a Google Maps Component. It creates
 * a Polygon component. It can also, for debug purposes, draw the region's bounding box around it.
 *
 * @param region the data from Region.
 * @param onPressed fired when the region is pressed.
 */
@Composable
fun RegionDrawer(region: RegionViewData, onPressed: () -> Unit = {}) {
    Polygon(
        points = region.polygon.geoLocations.toLatLngList(),
        onClick = { onPressed() },
        visible = region.active,
        holes = region.holes.map { hole -> hole.geoLocations.toLatLngList() },
        fillColor = Color.Transparent,
        clickable = true,
        zIndex = region.zIndex
    )
    if (showBoundingBoxPolygon) {
        BoundingBoxDrawer(boundingBox = region.boundingBox)
    }
}