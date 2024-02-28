package io.schiar.fridgnet.library.geocoder

import android.location.Geocoder
import io.schiar.fridgnet.Log
import io.schiar.fridgnet.model.Address
import io.schiar.fridgnet.model.Coordinate
import io.schiar.fridgnet.model.datasource.retriever.AddressRetriever
import android.location.Address as AndroidAddress

class AddressGeocoderRetriever(private val geocoder: Geocoder) : AddressRetriever {

    override fun fetchAddressBy(coordinate: Coordinate): Address? {
        val (latitude, longitude) = coordinate
        Log.d("Add Image Feature", "Getting address for ($latitude, $longitude)")
        val androidAddress = getAndroidAddress(latitude = latitude, longitude = longitude)
        val address = androidAddress?.toModelAddress()
        if (address != null) {
            Log.d(
                "Add Image Feature",
                "($latitude, $longitude) is ${address.name()}"
            )
        } else {
            Log.d(
                "Add Image Feature",
                "Couldn't find address of ($latitude, $longitude) }"
            )
        }
        return address
    }

    private fun getAndroidAddress(latitude: Double, longitude: Double): AndroidAddress? {
        var tries = 0
        while (tries <= 3) {
            try {
                @Suppress("DEPRECATION")
                val androidAddresses = geocoder.getFromLocation(
                    latitude,
                    longitude,
                    1
                )
                return androidAddresses?.firstOrNull()
            } catch (e: Exception) {
                Log.d(
                    "Add Image Feature",
                    "Error fetching address $e tries: $tries"
                )
                tries++
            }
        }
        return null
    }
}