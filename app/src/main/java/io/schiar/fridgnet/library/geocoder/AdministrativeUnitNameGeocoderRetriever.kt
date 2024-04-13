package io.schiar.fridgnet.library.geocoder

import android.location.Address
import android.location.Geocoder
import io.schiar.fridgnet.Log
import io.schiar.fridgnet.model.AdministrativeUnitName
import io.schiar.fridgnet.model.GeoLocation
import io.schiar.fridgnet.model.datasource.retriever.AdministrativeUnitNameRetriever
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 *  An implementation of the [AdministrativeUnitNameRetriever] interface that utilizes the provided
 *  [Geocoder] API to retrieve administrative unit information (e.g., city, state, country)
 *  based on geographical coordinates. This class operates asynchronously and emits successfully
 *  retrieved information through a returned [Flow] of [AdministrativeUnitName] objects.
 *
 *  **Retry Mechanism:** In case of geocoding failures, this class implements a retry mechanism
 *  with a maximum of 3 attempts. This helps mitigate potential transient issues with the geocoding
 *  service.
 *
 *  **Assumptions:** This class assumes the provided `GeoLocation` object has valid latitude and
 *  longitude coordinates.
 */
class AdministrativeUnitNameGeocoderRetriever @Inject constructor(
    private val geocoder: Geocoder
) : AdministrativeUnitNameRetriever {

    /**
     * Retrieves administrative unit information (e.g., city, state, country) for a given
     * geographical location using the provided [Geocoder] API. This class operates asynchronously
     * and emits successfully retrieved [AdministrativeUnitName] objects through the returned [Flow]
     * of [AdministrativeUnitName].
     *
     * In case of geocoding failures, the class implements a retry mechanism with a maximum of 3
     * attempts.
     *
     * @param geoLocation the geographical location (latitude and longitude) for which to retrieve
     * administrative unit information.
     * @return a [Flow] that emits successfully retrieved [AdministrativeUnitName] objects for the
     * provided location.
     */
    override fun retrieve(geoLocation: GeoLocation): Flow<AdministrativeUnitName> = flow {
        val (_, latitude, longitude) = geoLocation
        val address = withContext(Dispatchers.IO) {
            getAddress(latitude = latitude, longitude = longitude)
        }
        val administrativeUnitName = address?.toAdministrativeUnitName()
        if (administrativeUnitName != null) {
            log("$geoLocation is $administrativeUnitName")
            emit(administrativeUnitName)
        } else {
            log(msg = "Couldn't find administrative unit of $geoLocation }")
        }
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
                log(msg = "Error fetching administrative unit $e tries: $tries")
                tries++
            }
        }
        return null
    }

    private fun log(msg: String) {
        val methodName = Thread.currentThread().stackTrace[3].methodName
        Log.d(tag = "AdministrativeUnitNameGeocoderRetriever.$methodName", msg = msg)
    }
}