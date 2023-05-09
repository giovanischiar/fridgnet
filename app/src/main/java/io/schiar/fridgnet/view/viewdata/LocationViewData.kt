package io.schiar.fridgnet.view.viewdata

import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds

interface LocationViewData {
    val region: List<*>
    val boundingBox: LatLngBounds
}
data class LineStringLocationViewData(
    override val region: List<LatLng>,
    override val boundingBox: LatLngBounds
): LocationViewData

data class PolygonLocationViewData(
    override val region: List<List<LatLng>>,
    override val boundingBox: LatLngBounds
): LocationViewData

data class MultiPolygonLocationViewData(
    override val region: List<List<List<LatLng>>>,
    override val boundingBox: LatLngBounds
): LocationViewData