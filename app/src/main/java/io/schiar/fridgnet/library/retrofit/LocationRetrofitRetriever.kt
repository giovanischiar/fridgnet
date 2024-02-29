package io.schiar.fridgnet.library.retrofit

import io.schiar.fridgnet.Log
import io.schiar.fridgnet.model.Address
import io.schiar.fridgnet.model.AdministrativeUnit
import io.schiar.fridgnet.model.AdministrativeUnit.CITY
import io.schiar.fridgnet.model.AdministrativeUnit.COUNTRY
import io.schiar.fridgnet.model.AdministrativeUnit.COUNTY
import io.schiar.fridgnet.model.AdministrativeUnit.STATE
import io.schiar.fridgnet.model.Location
import io.schiar.fridgnet.model.datasource.retriever.LocationRetriever
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.withContext

class LocationRetrofitRetriever(private val nominatimAPI: NominatimAPI) : LocationRetriever {
    private var fetchingPlaces: Set<String> = emptySet()
    private var mutex: Mutex = Mutex()

    override suspend fun fetchLocationBy(address: Address): Location? {
        return when (address.administrativeUnit) {
            CITY -> fetchCity(address = address)
            COUNTY -> fetchCounty(address = address)
            STATE -> fetchState(address = address)
            COUNTRY -> fetchCountry(address = address)
        }
    }

    private suspend fun fetchCity(address: Address): Location? {
        val newAddress = if (address.locality == null) {
            extractAddress(address = address)
        } else {
            address
        }
        val addressStr = newAddress.name()
        if (fetchingPlaces.contains(addressStr)) return null
        fetchingPlaces = fetchingPlaces + addressStr
        return fetchLocation(address = newAddress, administrativeUnit = CITY)
    }

    private suspend fun fetchCounty(address: Address): Location? {
        address.subAdminArea ?: return null
        address.adminArea ?: return null
        address.countryName ?: return null
        val countyAddressName = address.name()
        if (fetchingPlaces.contains(countyAddressName)) return null
        fetchingPlaces = fetchingPlaces + countyAddressName
        return fetchLocation(address = address, administrativeUnit = COUNTY)
    }

    private suspend fun fetchState(address: Address): Location? {
        address.adminArea ?: return null
        address.countryName ?: return null
        val stateAddressName = address.name()
        if (fetchingPlaces.contains(stateAddressName)) return null
        fetchingPlaces = fetchingPlaces + stateAddressName
        return fetchLocation(address = address, administrativeUnit = STATE)
    }

    private suspend fun fetchCountry(address: Address): Location? {
        address.countryName ?: return null
        val countryAddressName = address.name()
        if (fetchingPlaces.contains(countryAddressName)) return null
        fetchingPlaces = fetchingPlaces + countryAddressName
        return fetchLocation(address = address, administrativeUnit = COUNTRY)
    }

    private fun extractAddress(address: Address): Address {
        val name = address.name()
        val add = name.split(", ")
        if (add.size < 2) return address
        val state = add[1]
        val city = add[0]
        return Address(
            locality = city,
            subAdminArea = null,
            adminArea = state,
            countryName = address.countryName
        )
    }

    private suspend fun fetchLocation(
        address: Address,
        administrativeUnit: AdministrativeUnit
    ): Location? {
        val city = address.locality ?: ""
        val county = address.subAdminArea ?: ""
        val state = address.adminArea ?: ""
        val country = address.countryName ?: ""
        mutex.lock()
        val jsonResult = withContext(Dispatchers.IO) {
            when (administrativeUnit) {
                CITY -> {
                    searchCity(city = city, state = state, country = country)
                }

                COUNTY -> {
                    nominatimAPI.getResultsCounty(county = county, state = state, country = country)
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
        }
        delay(1000) //Requests to Nominatim API should be limit to one per second
        mutex.unlock()
        jsonResult ?: return null
        Log.d(
            "API Result",
            "type: $administrativeUnit, address: ${address.name()} body.geojson: ${jsonResult.geoJSON}"
        )
        return jsonResult.toLocation(address = address, administrativeUnit = administrativeUnit)
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