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
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.sync.Mutex
import javax.inject.Inject

/**
 * Fetches cartographic boundary information using the Nominatim API. This class retrieves
 * boundaries based on provided administrative levels (e.g., city, state, country) and corresponding
 * names. It operates asynchronously and emits successfully retrieved [CartographicBoundary] objects
 * through a returned [Flow] of [CartographicBoundary].
 *
 * This class implements rate limiting with a one-second delay between Nominatim API requests to
 * comply with usage guidelines. It also uses a mutex for thread-safe access during API calls.
 *
 * Note: This class assumes the provided `administrativeUnitLevelAndAdministrativeUnitNameList`
 * contains valid combinations of `AdministrativeLevel` and `AdministrativeUnitName` objects.
 */
class CartographicBoundaryRetrofitRetriever @Inject constructor(
    private val nominatimAPI: NominatimAPI
) : CartographicBoundaryRetriever {
    private var fetchingPlaces: Set<String> = emptySet()
    private var mutex: Mutex = Mutex()

    /**
     * Retrieves a [Flow] of [CartographicBoundary] objects for a given list of
     * [AdministrativeLevel] and [AdministrativeUnitName] pairs. This method is designed to retrieve
     * boundaries for multiple locations at once.
     *
     * For each provided pair, the method attempts to fetch the corresponding cartographic boundary
     * based on the administrative level and name. If successful, the retrieved boundary information
     * is emitted through the returned [Flow].
     *
     * @param administrativeUnitLevelAndAdministrativeUnitNameList a list of pairs where the first
     * element specifies the administrative level (e.g., [CITY], [STATE], [COUNTRY]) and the second
     * element specifies the corresponding administrative unit name (e.g., "New York City",
     * "California").
     *
     * @return a [Flow] that emits successfully retrieved [CartographicBoundary] objects.
     */
    override fun retrieve(
        administrativeUnitLevelAndAdministrativeUnitNameList: List<
            Pair<AdministrativeLevel, AdministrativeUnitName>
        >
    ): Flow<CartographicBoundary> = flow {
        for ((administrativeUnitLevel, administrativeUnitName) in
            administrativeUnitLevelAndAdministrativeUnitNameList
        ) {
            log(
                msg = "Retrieving cartographic boundary for " +
                      "$administrativeUnitLevel $administrativeUnitName"
            )
            val cartographicBoundary = when(administrativeUnitLevel) {
                CITY -> retrieveLocality(administrativeUnitName = administrativeUnitName)
                COUNTY -> retrieveSubAdmin(administrativeUnitName = administrativeUnitName)
                STATE -> retrieveAdmin(administrativeUnitName = administrativeUnitName)
                COUNTRY -> retrieveCountry(administrativeUnitName = administrativeUnitName)
            } ?: continue
            emit(cartographicBoundary)
        }
    }

    private suspend fun retrieveLocality(
        administrativeUnitName: AdministrativeUnitName
    ): CartographicBoundary? {
        val newAdministrativeUnitName = if (administrativeUnitName.locality == null) {
            extractAdministrativeUnitName(administrativeUnitName = administrativeUnitName)
        } else {
            administrativeUnitName
        }
        val administrativeUnitNameString = "$newAdministrativeUnitName"
        if (fetchingPlaces.contains(administrativeUnitNameString)) return null
        fetchingPlaces = fetchingPlaces + administrativeUnitNameString
        return fetchCartographicBoundary(
            administrativeUnitName = newAdministrativeUnitName,
            administrativeLevel = CITY
        )
    }

    private suspend fun retrieveSubAdmin(
        administrativeUnitName: AdministrativeUnitName
    ): CartographicBoundary? {
        val administrativeUnitNameFullName = "$COUNTY " +
                administrativeUnitName.toString(administrativeLevel = COUNTY)
        administrativeUnitName.subAdminArea ?: run {
            log(msg = "Couldn't start retrieving cartographic boundary for " +
                    "$administrativeUnitNameFullName because subAdminArea field is null"
            )
            return null
        }
        administrativeUnitName.adminArea ?: run {
            log(
                msg = "Couldn't start retrieving cartographic boundary for " +
                        "$administrativeUnitNameFullName because adminArea field is null"
            )
            return null
        }
        administrativeUnitName.countryName ?: run {
            log(
                msg = "Couldn't start retrieving cartographic boundary for " +
                      "$administrativeUnitNameFullName because countryName field is null"
            )
            return null
        }
        val countyAdministrativeUnitNameString = administrativeUnitName.toString(
            administrativeLevel = COUNTY
        )
        if (fetchingPlaces.contains(countyAdministrativeUnitNameString)) return null
        fetchingPlaces = fetchingPlaces + countyAdministrativeUnitNameString
        return fetchCartographicBoundary(
            administrativeUnitName = administrativeUnitName,
            administrativeLevel = COUNTY
        )
    }

    private suspend fun retrieveAdmin(
        administrativeUnitName: AdministrativeUnitName
    ): CartographicBoundary? {
        val administrativeUnitNameFullName = "$STATE " +
                administrativeUnitName.toString(administrativeLevel = STATE)
        administrativeUnitName.adminArea ?: run {
            log(
                msg = "Couldn't start retrieving cartographic boundary for " +
                    "$administrativeUnitNameFullName because adminArea field is null")
            return null
        }
        administrativeUnitName.countryName ?: run {
            log(
                msg = "Couldn't start retrieving cartographic boundary for " +
                      "$administrativeUnitNameFullName because countryName field is null"
            )
            return null
        }
        val stateAdministrativeUnitNameString = administrativeUnitName.toString(
            administrativeLevel = STATE
        )
        if (fetchingPlaces.contains(stateAdministrativeUnitNameString)) return null
        fetchingPlaces = fetchingPlaces + stateAdministrativeUnitNameString
        return fetchCartographicBoundary(
            administrativeUnitName = administrativeUnitName,
            administrativeLevel = STATE
        )
    }

    private suspend fun retrieveCountry(
        administrativeUnitName: AdministrativeUnitName
    ): CartographicBoundary? {
        val administrativeUnitNameFullName = "$COUNTRY " +
                administrativeUnitName.toString(administrativeLevel = COUNTRY)
        administrativeUnitName.countryName ?: run {
            log(
                msg = "Couldn't start retrieving cartographic boundary for " +
                      "$administrativeUnitNameFullName because countryName field is null"
            )
            return null
        }
        val countryAdministrativeUnitNameString = administrativeUnitName.toString(
            administrativeLevel = COUNTRY
        )
        if (fetchingPlaces.contains(countryAdministrativeUnitNameString)) return null
        fetchingPlaces = fetchingPlaces + countryAdministrativeUnitNameString
        return fetchCartographicBoundary(
            administrativeUnitName = administrativeUnitName,
            administrativeLevel = COUNTRY
        )
    }

    private fun extractAdministrativeUnitName(
        administrativeUnitName: AdministrativeUnitName
    ): AdministrativeUnitName {
        val name = "$administrativeUnitName"
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
        mutex.lock() // This is to ensure there is only one thread using the API per time
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
        delay(1000) // Requests to Nominatim API should be limit to one per second.
        mutex.unlock()
        val administrativeUnitNameFullName = "$administrativeLevel " +
                administrativeUnitName.toString(administrativeLevel = administrativeLevel)
        if (jsonResult == null) {
            log(
                msg = "There is no cartographic boundary for $administrativeUnitNameFullName"
            )
            return null
        }
        val cartographicBoundary = jsonResult.toCartographicBoundary(
            administrativeUnitName = administrativeUnitName,
            administrativeLevel = administrativeLevel
        )
        log(
            msg = "$administrativeUnitNameFullName is $cartographicBoundary"
        )
        return cartographicBoundary
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
            // Check if there are second result that contains a MultiPolygon.
            val jsonSecondResult = jsonResults[1]
            if (
                jsonSecondResult.displayName == jsonFirstResult.displayName &&
                jsonSecondResult.type == "administrative"
            ) {
                if (jsonSecondResult.geoJSON.type == "MultiPolygon") {
                    log(
                        msg = "The second API result's geoJSON type is MultiPolygon, returning " +
                              "this one instead"
                    )
                    return jsonSecondResult
                }
            }
        }

        if (type == "Point") {
            if (jsonResults.size == 1) {
                // Point is not a valid Cartographic Boundary, using the q param provided for the
                // API.
                log(msg = "The API geoJSON type is a point, trying to using the q instead")
                return nominatimAPI.getResults(q = "$city, $state, $country")[0]
            }
            val jsonSecondResult = jsonResults[1]
            if (
                jsonSecondResult.displayName == jsonFirstResult.displayName &&
                jsonSecondResult.type == "administrative"
            ) {
                // There are a second result with type administrative.
                log(
                    msg = "The second API result's type is administrative, " +
                           "returning this one instead"
                )
                return jsonSecondResult
            }
            return nominatimAPI.getResults(q = "$city, $state, $country")[0]
        }

        return jsonFirstResult
    }

    private fun log(msg: String) {
        val methodName = Thread.currentThread().stackTrace[3].methodName
        Log.d(tag = "CartographicBoundaryRetrofitRetriever.$methodName", msg = msg)
    }
}