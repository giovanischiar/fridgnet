package io.schiar.fridgnet.view.util

import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import io.schiar.fridgnet.view.viewdata.BoundingBoxViewData
import io.schiar.fridgnet.view.viewdata.CoordinateViewData

// CoordinateViewData
fun CoordinateViewData.toLatLng(): LatLng {
    return LatLng(latitude, longitude)
}

fun List<CoordinateViewData>.toLatLngList(): List<LatLng> {
    return map { it.toLatLng() }
}

// BoundingBoxViewData
fun BoundingBoxViewData.toLatLngBounds(): LatLngBounds {
    return LatLngBounds(this.northeast.toLatLng(), this.southwest.toLatLng())
}