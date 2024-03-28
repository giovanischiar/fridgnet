package io.schiar.fridgnet.view.util

import android.graphics.BitmapFactory
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.MapUiSettings
import io.schiar.fridgnet.view.viewdata.BoundingBoxViewData
import io.schiar.fridgnet.view.viewdata.GeoLocationViewData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

// GeoLocationViewData
fun GeoLocationViewData.toLatLng(): LatLng {
    return LatLng(latitude, longitude)
}

fun List<GeoLocationViewData>.toLatLngList(): List<LatLng> {
    return map { it.toLatLng() }
}

// BoundingBoxViewData
fun BoundingBoxViewData.toLatLngBounds(): LatLngBounds {
    return LatLngBounds(southwest.toLatLng(), northeast.toLatLng())
}

fun LatLng.toGeoLocationViewData(): GeoLocationViewData {
    return GeoLocationViewData(latitude = latitude, longitude = longitude)
}

fun LatLngBounds.toBoundingBoxViewData(): BoundingBoxViewData {
    return BoundingBoxViewData(
        southwest = southwest.toGeoLocationViewData(),
        northeast = northeast.toGeoLocationViewData()
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

fun ByteArray.toImageBitmap(): ImageBitmap {
    return BitmapFactory.decodeByteArray(this, 0, this.size).asImageBitmap()
}

fun ByteArray.toBitmapDescriptor(): BitmapDescriptor {
    return BitmapDescriptorFactory.fromBitmap(
        BitmapFactory.decodeByteArray(this, 0, this.size)
    )
}

fun CameraPositionState.updateCameraPositionTo(
    boundingBox: BoundingBoxViewData?,
    coroutineScope: CoroutineScope,
    animate: Boolean = false,
    padding: Int = 2
) {
    boundingBox ?: return
    val latLngBounds = boundingBox.toLatLngBounds()
    val cameraUpdate = CameraUpdateFactory.newLatLngBounds(latLngBounds, padding)
    if (animate) {
        coroutineScope.launch(Dispatchers.Main) {
            animate(cameraUpdate, durationMs = 1000)
        }
        return
    }
    move(cameraUpdate)
}
