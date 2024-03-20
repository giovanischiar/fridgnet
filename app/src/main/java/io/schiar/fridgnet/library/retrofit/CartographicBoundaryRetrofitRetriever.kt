package io.schiar.fridgnet.library.retrofit

import io.schiar.fridgnet.Log
import io.schiar.fridgnet.model.AdministrativeLevel
import io.schiar.fridgnet.model.AdministrativeLevel.CITY
import io.schiar.fridgnet.model.AdministrativeLevel.COUNTRY
import io.schiar.fridgnet.model.AdministrativeLevel.COUNTY
import io.schiar.fridgnet.model.AdministrativeLevel.STATE
import io.schiar.fridgnet.model.AdministrativeUnitName
import io.schiar.fridgnet.model.CartographicBoundary
import io.schiar.fridgnet.model.datasource.retriever.CartographicBoundaryRetriever
import kotlinx.coroutines.delay
import kotlinx.coroutines.sync.Mutex

class CartographicBoundaryRetrofitRetriever(
    private val nominatimAPI: NominatimAPI
) : CartographicBoundaryRetriever {
    private var fetchingPlaces: Set<String> = emptySet()
    private var mutex: Mutex = Mutex()

    override suspend fun retrieveLocality(
        administrativeUnitName: AdministrativeUnitName
    ): CartographicBoundary? {
        val newAdministrativeUnitName = if (administrativeUnitName.locality == null) {
            extractAdministrativeUnitName(administrativeUnitName = administrativeUnitName)
        } else {
            administrativeUnitName
        }
        val administrativeUnitNameStr = newAdministrativeUnitName.name()
        if (fetchingPlaces.contains(administrativeUnitNameStr)) return null
        fetchingPlaces = fetchingPlaces + administrativeUnitNameStr
        return fetchCartographicBoundary(
            administrativeUnitName = newAdministrativeUnitName,
            administrativeLevel = CITY
        )
    }

    override suspend fun retrieveSubAdmin(
        administrativeUnitName: AdministrativeUnitName
    ): CartographicBoundary? {
        administrativeUnitName.subAdminArea ?: return null
        administrativeUnitName.adminArea ?: return null
        administrativeUnitName.countryName ?: return null
        val countyAdministrativeUnitNameName = administrativeUnitName.name(
            administrativeLevel = COUNTY
        )
        if (fetchingPlaces.contains(countyAdministrativeUnitNameName)) return null
        fetchingPlaces = fetchingPlaces + countyAdministrativeUnitNameName
        return fetchCartographicBoundary(
            administrativeUnitName = administrativeUnitName,
            administrativeLevel = COUNTY
        )
    }

    override suspend fun retrieveAdmin(
        administrativeUnitName: AdministrativeUnitName
    ): CartographicBoundary? {
        administrativeUnitName.adminArea ?: return null
        administrativeUnitName.countryName ?: return null
        val stateAdministrativeUnitNameName = administrativeUnitName.name(administrativeLevel = STATE)
        if (fetchingPlaces.contains(stateAdministrativeUnitNameName)) return null
        fetchingPlaces = fetchingPlaces + stateAdministrativeUnitNameName
        return fetchCartographicBoundary(
            administrativeUnitName = administrativeUnitName,
            administrativeLevel = STATE
        )
    }

    override suspend fun retrieveCountry(
        administrativeUnitName: AdministrativeUnitName
    ): CartographicBoundary? {
        administrativeUnitName.countryName ?: return null
        val countryAdministrativeUnitNameName = administrativeUnitName.name(administrativeLevel = COUNTRY)
        if (fetchingPlaces.contains(countryAdministrativeUnitNameName)) return null
        fetchingPlaces = fetchingPlaces + countryAdministrativeUnitNameName
        return fetchCartographicBoundary(
            administrativeUnitName = administrativeUnitName,
            administrativeLevel = COUNTRY
        )
    }

    private fun extractAdministrativeUnitName(
        administrativeUnitName: AdministrativeUnitName
    ): AdministrativeUnitName {
        val name = administrativeUnitName.name()
        val add = name.split(", ")
        if (add.size < 2) return administrativeUnitName
        val state = add[1]
        val city = add[0]
        return AdministrativeUnitName(
            locality = city,
            subAdminArea = null,
            adminArea = state,
            countryName = administrativeUnitName.countryName
        )
    }

    private suspend fun fetchCartographicBoundary(
        administrativeUnitName: AdministrativeUnitName,
        administrativeLevel: AdministrativeLevel
    ): CartographicBoundary? {
        val city = administrativeUnitName.locality ?: ""
        val county = administrativeUnitName.subAdminArea ?: ""
        val state = administrativeUnitName.adminArea ?: ""
        val country = administrativeUnitName.countryName ?: ""
        mutex.lock()
        val jsonResult = try {
            when (administrativeLevel) {
                CITY -> {
                    searchCity(city = city, state = state, country = country)
                }

                COUNTY -> {
                    nominatimAPI.getResultsCounty(
                        county = county,
                        state = state,
                        country = country
                    )
                        .getOrNull(index = 0)
                }

                STATE -> {
                    nominatimAPI.getResultsState(state = state, country = country)
                        .getOrNull(index = 0)
                }

                COUNTRY -> {
                    nominatimAPI.getResultsCountry(country = country)
                        .getOrNull(index = 0)
                }
            }
        } catch (exception: Exception) {
            Log.d("API Result", "error: $exception")
            null
        }
        delay(1000) //Requests to Nominatim API should be limit to one per second
        mutex.unlock()
        jsonResult ?: return null
        Log.d(
            tag = "API Result",
            msg = "type: $administrativeLevel, " +
                  "administrativeUnitName: ${administrativeUnitName.name()} " +
                  "body.geojson: ${jsonResult.geoJSON}"
        )
        return jsonResult.toCartographicBoundary(
            administrativeUnitName = administrativeUnitName,
            administrativeLevel = administrativeLevel
        )
    }

    private suspend fun searchCity(
        city: String, state: String, country: String
    ): JSONResult<GeoJSONAttributes>? {
        val jsonResults = nominatimAPI.getResultsCity(
            city = city, state = state, country = country
        )
        val jsonFirstResult = jsonResults.getOrNull(index = 0) ?: return null
        val geoJSON = jsonFirstResult.geoJSON
        val type = geoJSON.type

        if (type == "Polygon") {
            if (jsonResults.size == 1) return jsonFirstResult
            val jsonSecondResult = jsonResults[1]
            if (
                jsonSecondResult.displayName == jsonFirstResult.displayName &&
                jsonSecondResult.type == "administrative"
            ) {
                if (jsonSecondResult.geoJSON.type == "MultiPolygon") {
                    return jsonSecondResult
                }
            }
        }

        if (type == "Point") {
            if (jsonResults.size == 1) {
                Log.d("Search for API Polygon", "Trying to using the q")
                return nominatimAPI.getResults(q = "$city, $state, $country")[0]
            }
            val jsonSecondResult = jsonResults[1]
            Log.d(
                "Search for API Polygon",
                "Second Result name: {$jsonSecondResult.name} type: {$jsonSecondResult.type}"
            )
            if (
                jsonSecondResult.displayName == jsonFirstResult.displayName &&
                jsonSecondResult.type == "administrative"
            ) {
                Log.d("Search for API Polygon", "Second body is the administrative")
                return jsonSecondResult
            }
            return nominatimAPI.getResults(q = "$city, $state, $country")[0]
        }

        return jsonFirstResult
    }
}