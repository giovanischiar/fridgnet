package io.schiar.fridgnet.view.shared.util

import android.content.Context
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
import io.schiar.fridgnet.R
import io.schiar.fridgnet.view.shared.viewdata.AdministrativeLevelViewData
import io.schiar.fridgnet.view.shared.viewdata.BoundingBoxViewData
import io.schiar.fridgnet.view.shared.viewdata.GeoLocationViewData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Converts the [GeoLocationViewData] into the Google Maps [LatLng] object.
 */
fun GeoLocationViewData.toLatLng(): LatLng {
    return LatLng(latitude, longitude)
}

/**
 * Converts the list of [GeoLocationViewData] into the list Google Maps [LatLng] objects.
 */
fun List<GeoLocationViewData>.toLatLngList(): List<LatLng> {
    return map { it.toLatLng() }
}

/**
 * Converts the [BoundingBoxViewData] into the Google Maps [LatLngBounds] object.
 */
fun BoundingBoxViewData.toLatLngBounds(): LatLngBounds {
    return LatLngBounds(southwest.toLatLng(), northeast.toLatLng())
}

/**
 * Converts back the Google Maps [LatLng] into the [BoundingBoxViewData] object.
 */
fun LatLng.toGeoLocationViewData(): GeoLocationViewData {
    return GeoLocationViewData(latitude = latitude, longitude = longitude)
}

/**
 * Converts back the Google Maps [LatLngBounds] into the [BoundingBoxViewData] object.
 */
fun LatLngBounds.toBoundingBoxViewData(): BoundingBoxViewData {
    return BoundingBoxViewData(
        southwest = southwest.toGeoLocationViewData(),
        northeast = northeast.toGeoLocationViewData()
    )
}

/**
 * Default static settings of the Google Maps [MapUiSettings]. Used to prevent User to interact to
 * the map used in the Administrative Units screen.
 */
fun MapUiSettingsStatic(): MapUiSettings {
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

/**
 * Converts the [ByteArray] into the Android [ImageBitmap] object.
 */
fun ByteArray.toImageBitmap(): ImageBitmap {
    return BitmapFactory.decodeByteArray(this, 0, this.size).asImageBitmap()
}

/**
 * Converts the [ByteArray] into the Android [BitmapDescriptor] object.
 */
fun ByteArray.toBitmapDescriptor(): BitmapDescriptor {
    return BitmapDescriptorFactory.fromBitmap(
        BitmapFactory.decodeByteArray(this, 0, this.size)
    )
}

/**
 * Commands the CameraPositionState to update its position in the map to focus on a specific area.
 *
 * @param boundingBox the bounding box containing the target area to move the camera to. If null,
 *        the function does nothing.
 * @param coroutineScope used to launch a coroutine for animated camera movements.
 * @param animate whether to animate the camera movement. Defaults to false.
 * @param padding the padding (in dp) applied around the bounding box when framing the camera view.
 *        Defaults to 2.
 * @param onMoveFinished a callback fired when the camera movement finishes (including animations).
 *        Defaults to an empty function.
 */
fun CameraPositionState.updateCameraPositionTo(
    boundingBox: BoundingBoxViewData?,
    coroutineScope: CoroutineScope,
    animate: Boolean = false,
    padding: Int = 2,
    onMoveFinished: () -> Unit = {}
) {
    boundingBox ?: return
    val latLngBounds = boundingBox.toLatLngBounds()
    val cameraUpdate = CameraUpdateFactory.newLatLngBounds(latLngBounds, padding)
    if (animate) {
        coroutineScope.launch(Dispatchers.Main) {
            animate(cameraUpdate, durationMs = 1000)
            onMoveFinished()
        }
        return
    }
    onMoveFinished()
    move(cameraUpdate)
}

/**
 * Gets the localized string resource for the administrative level based on its title.
 *
 * @param context the Android object needed to access the resources object.
 * @return the localized string from the resource ID.
 */
fun AdministrativeLevelViewData.getResourceString(context: Context): String {
    return context.resources.getString(when(this.title) {
            "CITY" -> R.string.cities
            "COUNTY" -> R.string.counties
            "STATE" -> R.string.states
            "COUNTRY" -> R.string.countries
            else -> R.string.cities
        }
    )
}