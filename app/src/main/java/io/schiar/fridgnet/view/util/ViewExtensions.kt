package io.schiar.fridgnet.view.util

import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import io.schiar.fridgnet.model.Address
import io.schiar.fridgnet.view.viewdata.LocationViewData
import android.location.Address as SystemAddress

fun LocationViewData.toLatLng(): LatLng {
    return LatLng(lat.toDouble(), lng.toDouble())
}

fun List<Double>.toLatLng(): LatLng {
    return LatLng(this[1], this[0])
}

fun List<List<Double>>.toListLatLng(): List<LatLng> {
    return map { it.toLatLng() }
}

fun List<List<List<Double>>>.toMatrixLatLng(): List<List<LatLng>> {
    return map { it.toListLatLng() }
}

fun List<List<List<List<Double>>>>.toListOfPolygon(): List<List<List<LatLng>>> {
    return map { it.toMatrixLatLng() }
}

fun List<String>.toLatLngBounds(): LatLngBounds {
    val builder = LatLngBounds.builder()
    builder.include(LatLng(this[0].toDouble(), this[2].toDouble()))
    builder.include(LatLng(this[1].toDouble(), this[3].toDouble()))
    return builder.build()
}

fun SystemAddress.toAddress(): Address {
    return Address(
        locality = this.locality,
        subAdminArea = this.subAdminArea,
        adminArea = this.adminArea,
        countryName = this.countryName
    )
}

fun Address.name(): String {
    return if (this.locality != null) {
        "${this.locality}, ${this.subAdminArea}, ${this.adminArea}, ${this.countryName}"
    } else if (this.subAdminArea != null) {
        "${this.subAdminArea}, ${this.adminArea}, ${this.countryName}"
    } else if (this.adminArea != null) {
        "${this.adminArea}, ${this.countryName}"
    } else this.countryName ?: "null"
}