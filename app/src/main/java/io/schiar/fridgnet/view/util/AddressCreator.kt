package io.schiar.fridgnet.view.util

import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.util.Log
import java.util.*

class AddressCreator {
    fun addressFromLocation(context: Context, latitude: Double, longitude: Double): Address {
        val geocoder = Geocoder(context, Locale.US)
        var tries = 0
        while (tries <= 3) {
            try {
                val addresses = geocoder.getFromLocation(
                    latitude,
                    longitude,
                    1
                )

                return if (addresses?.size!! > 0) addresses[0] else Address(Locale.US)
            } catch (e: Exception) {
                Log.d("API Result", "Error fetching address $e tries: $tries")
                tries++
            }
        }

        return Address(Locale.US)
    }
}