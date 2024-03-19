package io.schiar.fridgnet.library.geocoder

import android.location.Address
import android.location.Geocoder
import io.schiar.fridgnet.Log
import io.schiar.fridgnet.model.AdministrativeUnit
import io.schiar.fridgnet.model.GeoLocation
import io.schiar.fridgnet.model.datasource.retriever.AdministrativeUnitRetriever
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AdministrativeUnitGeocoderRetriever(
    private val geocoder: Geocoder
) : AdministrativeUnitRetriever {

    override suspend fun retrieve(geoLocation: GeoLocation): AdministrativeUnit? {
        val (_, latitude, longitude) = geoLocation
        Log.d(
            tag = "Add Image Feature",
            msg = "Getting administrative unit for ($latitude, $longitude)"
        )
        val address = withContext(Dispatchers.IO) {
            getAddress(latitude = latitude, longitude = longitude)
        }
        val administrativeUnit = address?.toAdministrativeUnit()
        if (administrativeUnit != null) {
            Log.d(
                "AdministrativeUnitGeocoderRetriever.Add Image Feature",
                "($latitude, $longitude) is ${administrativeUnit.name()}"
            )
        } else {
            Log.d(
                "Add Image Feature",
                "Couldn't find administrative unit of ($latitude, $longitude) }"
            )
        }
        return administrativeUnit
    }

    private fun getAddress(latitude: Double, longitude: Double): Address? {
        var tries = 0
        while (tries <= 3) {
            try {
                @Suppress("DEPRECATION")
                var androidAdministrativeUnits = geocoder.getFromLocation(
                    latitude,
                    longitude,
                    1
                )
                val androidAdministrativeUnit = androidAdministrativeUnits
                    ?.firstOrNull() ?: return null
                if (androidAdministrativeUnit.countryName == "United States") {
                    val administrativeUnitName =
                        androidAdministrativeUnit.locality + ", "
                        androidAdministrativeUnit.adminArea
                    @Suppress("DEPRECATION")
                    androidAdministrativeUnits = geocoder.getFromLocationName(
                        administrativeUnitName,
                        1
                    )
                }
                return androidAdministrativeUnits?.firstOrNull()
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