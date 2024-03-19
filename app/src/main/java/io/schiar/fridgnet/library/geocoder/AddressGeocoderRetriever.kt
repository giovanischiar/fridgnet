package io.schiar.fridgnet.library.geocoder

import android.location.Geocoder
import io.schiar.fridgnet.Log
import io.schiar.fridgnet.model.Address
import io.schiar.fridgnet.model.GeoLocation
import io.schiar.fridgnet.model.datasource.retriever.AddressRetriever
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import android.location.Address as AndroidAddress

class AddressGeocoderRetriever(private val geocoder: Geocoder) : AddressRetriever {

    override suspend fun retrieve(geoLocation: GeoLocation): Address? {
        val (_, latitude, longitude) = geoLocation
        Log.d("Add Image Feature", "Getting address for ($latitude, $longitude)")
        val androidAddress = withContext(Dispatchers.IO) {
            getAndroidAddress(latitude = latitude, longitude = longitude)
        }
        val address = androidAddress?.toModelAddress()
        if (address != null) {
            Log.d(
                "AddressGeocoderRetriever.Add Image Feature",
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
                var androidAddresses = geocoder.getFromLocation(
                    latitude,
                    longitude,
                    1
                )
                val androidAddress = androidAddresses?.firstOrNull() ?: return null
                if (androidAddress.countryName == "United States") {
                    val addressName = "${androidAddress.locality}, ${androidAddress.adminArea}"
                    @Suppress("DEPRECATION")
                    androidAddresses = geocoder.getFromLocationName(addressName, 1)
                }
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