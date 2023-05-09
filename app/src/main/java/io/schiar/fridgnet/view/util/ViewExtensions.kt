package io.schiar.fridgnet.view.util

import android.graphics.Point
import com.google.android.gms.maps.model.LatLng
import io.schiar.fridgnet.view.viewdata.CoordinateViewData
import kotlin.math.roundToInt

fun CoordinateViewData.toLatLng(): LatLng {
    return LatLng(lat.toDouble(), lng.toDouble())
}

fun LatLng.toPoint(): Point {
    return Point(this.latitude.roundToInt(), this.longitude.roundToInt())
}