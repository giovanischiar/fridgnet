package io.schiar.fridgnet.view.util

import android.content.Context
import android.location.Geocoder
import java.util.*

class AddressCreator {
    fun addressFromLocation(context: Context, latitude: Double, longitude: Double): String {
        val geocoder = Geocoder(context, Locale.getDefault())
        val addresses = geocoder.getFromLocation(
            latitude.toDouble(),
            longitude.toDouble(),
            1
        );
        return if (addresses?.size!! > 0) {
            "${addresses[0].locality}, ${addresses[0].subAdminArea}, ${addresses[0].adminArea}, ${addresses[0].countryName}"
        } else { "null" }
    }
}