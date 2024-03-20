package io.schiar.fridgnet.library.geocoder

import android.location.Address
import android.location.Geocoder
import io.schiar.fridgnet.Log
import io.schiar.fridgnet.model.AdministrativeUnitName
import io.schiar.fridgnet.model.GeoLocation
import io.schiar.fridgnet.model.datasource.retriever.AdministrativeUnitNameRetriever
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AdministrativeUnitNameGeocoderRetriever(
    private val geocoder: Geocoder
) : AdministrativeUnitNameRetriever {

    override suspend fun retrieve(geoLocation: GeoLocation): AdministrativeUnitName? {
        val (_, latitude, longitude) = geoLocation
        Log.d(
            tag = "Add Image Feature",
            msg = "Getting administrative unit for ($latitude, $longitude)"
        )
        val address = withContext(Dispatchers.IO) {
            getAddress(latitude = latitude, longitude = longitude)
        }
        val administrativeUnitName = address?.toAdministrativeUnitName()
        if (administrativeUnitName != null) {
            Log.d(
                "AdministrativeUnitNameGeocoderRetriever.Add Image Feature",
                "($latitude, $longitude) is $administrativeUnitName"
            )
        } else {
            Log.d(
                "Add Image Feature",
                "Couldn't find administrative unit of ($latitude, $longitude) }"
            )
        }
        return administrativeUnitName
    }

    private fun getAddress(latitude: Double, longitude: Double): Address? {
        var tries = 0
        while (tries <= 3) {
            try {
                @Suppress("DEPRECATION")
                var androidAdministrativeUnitNames = geocoder.getFromLocation(
                    latitude,
                    longitude,
                    1
                )
                val androidAdministrativeUnitName = androidAdministrativeUnitNames
                    ?.firstOrNull() ?: return null
                if (androidAdministrativeUnitName.countryName == "United States") {
                    val administrativeUnitNameName =
                        androidAdministrativeUnitName.locality + ", "
                        androidAdministrativeUnitName.adminArea
                    @Suppress("DEPRECATION")
                    androidAdministrativeUnitNames = geocoder.getFromLocationName(
                        administrativeUnitNameName,
                        1
                    )
                }
                return androidAdministrativeUnitNames?.firstOrNull()
            } catch (e: Exception) {
                Log.d(
                    "Add Image Feature",
                    "Error fetching administrative unit $e tries: $tries"
                )
                tries++
            }
        }
        return null
    }
}