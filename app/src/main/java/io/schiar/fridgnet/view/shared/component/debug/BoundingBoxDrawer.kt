package io.schiar.fridgnet.view.shared.component.debug

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.Polygon
import io.schiar.fridgnet.view.shared.viewdata.BoundingBoxViewData

/**
 * Component used for debugging purposes. It visually overlays a rectangle on the map
 * to represent the provided bounding box data.
 *
 * @param boundingBox the bounding box data used for drawing.
 */
@Composable
fun BoundingBoxDrawer(boundingBox: BoundingBoxViewData) {
    Polygon(
        points = listOf(
            LatLng(boundingBox.northeast.latitude, boundingBox.southwest.longitude),
            LatLng(boundingBox.northeast.latitude, boundingBox.northeast.longitude),
            LatLng(boundingBox.southwest.latitude, boundingBox.northeast.longitude),
            LatLng(boundingBox.southwest.latitude, boundingBox.southwest.longitude),
        ),
        fillColor = Color.Transparent
    )
}