package io.schiar.fridgnet.library.retrofit

import io.schiar.fridgnet.Log
import io.schiar.fridgnet.model.AdministrativeUnit
import io.schiar.fridgnet.model.AdministrativeLevel
import io.schiar.fridgnet.model.AdministrativeLevel.CITY
import io.schiar.fridgnet.model.AdministrativeLevel.COUNTRY
import io.schiar.fridgnet.model.AdministrativeLevel.COUNTY
import io.schiar.fridgnet.model.AdministrativeLevel.STATE
import io.schiar.fridgnet.model.Location
import io.schiar.fridgnet.model.datasource.retriever.LocationRetriever
import kotlinx.coroutines.delay
import kotlinx.coroutines.sync.Mutex

class LocationRetrofitRetriever(private val nominatimAPI: NominatimAPI) : LocationRetriever {
    private var fetchingPlaces: Set<String> = emptySet()
    private var mutex: Mutex = Mutex()

    override suspend fun retrieveLocality(administrativeUnit: AdministrativeUnit): Location? {
        val newAdministrativeUnit = if (administrativeUnit.locality == null) {
            extractAdministrativeUnit(administrativeUnit = administrativeUnit)
        } else {
            administrativeUnit
        }
        val administrativeUnitStr = newAdministrativeUnit.name()
        if (fetchingPlaces.contains(administrativeUnitStr)) return null
        fetchingPlaces = fetchingPlaces + administrativeUnitStr
        return fetchLocation(administrativeUnit = newAdministrativeUnit, administrativeLevel = CITY)
    }

    override suspend fun retrieveSubAdmin(administrativeUnit: AdministrativeUnit): Location? {
        administrativeUnit.subAdminArea ?: return null
        administrativeUnit.adminArea ?: return null
        administrativeUnit.countryName ?: return null
        val countyAdministrativeUnitName = administrativeUnit.name(administrativeLevel = COUNTY)
        if (fetchingPlaces.contains(countyAdministrativeUnitName)) return null
        fetchingPlaces = fetchingPlaces + countyAdministrativeUnitName
        return fetchLocation(administrativeUnit = administrativeUnit, administrativeLevel = COUNTY)
    }

    override suspend fun retrieveAdmin(administrativeUnit: AdministrativeUnit): Location? {
        administrativeUnit.adminArea ?: return null
        administrativeUnit.countryName ?: return null
        val stateAdministrativeUnitName = administrativeUnit.name(administrativeLevel = STATE)
        if (fetchingPlaces.contains(stateAdministrativeUnitName)) return null
        fetchingPlaces = fetchingPlaces + stateAdministrativeUnitName
        return fetchLocation(administrativeUnit = administrativeUnit, administrativeLevel = STATE)
    }

    override suspend fun retrieveCountry(administrativeUnit: AdministrativeUnit): Location? {
        administrativeUnit.countryName ?: return null
        val countryAdministrativeUnitName = administrativeUnit.name(administrativeLevel = COUNTRY)
        if (fetchingPlaces.contains(countryAdministrativeUnitName)) return null
        fetchingPlaces = fetchingPlaces + countryAdministrativeUnitName
        return fetchLocation(administrativeUnit = administrativeUnit, administrativeLevel = COUNTRY)
    }

    private fun extractAdministrativeUnit(administrativeUnit: AdministrativeUnit): AdministrativeUnit {
        val name = administrativeUnit.name()
        val add = name.split(", ")
        if (add.size < 2) return administrativeUnit
        val state = add[1]
        val city = add[0]
        return AdministrativeUnit(
            locality = city,
            subAdminArea = null,
            adminArea = state,
            countryName = administrativeUnit.countryName
        )
    }

    private suspend fun fetchLocation(
        administrativeUnit: AdministrativeUnit,
        administrativeLevel: AdministrativeLevel
    ): Location? {
        val city = administrativeUnit.locality ?: ""
        val county = administrativeUnit.subAdminArea ?: ""
        val state = administrativeUnit.adminArea ?: ""
        val country = administrativeUnit.countryName ?: ""
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
            "API Result",
            "type: $administrativeLevel, administrativeUnit: ${administrativeUnit.name()} body.geojson: ${jsonResult.geoJSON}"
        )
        return jsonResult.toLocation(administrativeUnit = administrativeUnit, administrativeLevel = administrativeLevel)
    }

    private suspend fun searchCity(
        city: String, state: String, country: String
    ): JSONResult<GeoJSONAttributes>? {
        val jsonResults = nominatimAPI.getResultsCity(city = city, state = state, country = country)
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