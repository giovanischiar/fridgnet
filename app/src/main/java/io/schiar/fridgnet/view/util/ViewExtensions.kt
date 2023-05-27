package io.schiar.fridgnet.view.util

import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.maps.android.compose.MapUiSettings
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
    return LatLngBounds(southwest.toLatLng(), northeast.toLatLng())
}

fun LatLng.toCoordinateViewData(): CoordinateViewData {
    return CoordinateViewData(latitude = latitude, longitude = longitude)
}

fun LatLngBounds.toBoundingBoxViewData(): BoundingBoxViewData {
    return BoundingBoxViewData(
        southwest = southwest.toCoordinateViewData(),
        northeast = northeast.toCoordinateViewData()
    )
}

fun MapUiSettings.static(): MapUiSettings {
    return MapUiSettings(
        compassEnabled = false,
        indoorLevelPickerEnabled = false,
        mapToolbarEnabled = false,
        myLocationButtonEnabled = false,
        rotationGesturesEnabled = false,
        scrollGesturesEnabled = false,
        scrollGesturesEnabledDuringRotateOrZoom = false,
        tiltGesturesEnabled = false,
        zoomControlsEnabled = false,
        zoomGesturesEnabled = false
    )
}