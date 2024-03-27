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

class AdministrativeUnitNameGeocoderRetriever @Inject constructor(
    private val geocoder: Geocoder
) : AdministrativeUnitNameRetriever {

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