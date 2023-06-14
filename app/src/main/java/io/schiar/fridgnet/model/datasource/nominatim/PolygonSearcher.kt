package io.schiar.fridgnet.model.datasource.nominatim

import io.schiar.fridgnet.Log
import retrofit2.Response

typealias APIResponse = Response<List<Result<GeoJsonAttributes>>>

class PolygonSearcher {
    suspend fun searchCity(city: String, state: String, country: String): APIResponse {
        val quotesApi = RetrofitHelper.getInstance().create(NominatimApi::class.java)
        val result = quotesApi.getResultsCity(city = city, state = state, country = country)
        val bodies = result.body() ?: return result
        val body = if (bodies.isEmpty()) return result else bodies[0]
        val geoJSON = body.geojson
        val type = geoJSON.type

        if (type == "Polygon") {
            if (bodies.size == 1) return result
            val secondBody = bodies[1]
            if (
                secondBody.display_name == body.display_name && secondBody.type == "administrative"
            ) {
                if (secondBody.geojson.type == "MultiPolygon") {
                    return Response.success(listOf(secondBody))
                }
            }
        }

        if (type == "Point") {
            if (bodies.size == 1) {
                Log.d("Search for API Polygon", "Trying to using the q")
                return quotesApi.getResults(q = "$city, $state, $country")
            }
            val secondBody =  bodies[1]
            Log.d(
                "Search for API Polygon",
                "Second Result name: {$secondBody.name} type: {$secondBody.type}"
            )
            if (
                secondBody.display_name == body.display_name && secondBody.type == "administrative"
            ) {
                Log.d("Search for API Polygon", "Second body is the administrative")
                return Response.success(listOf(secondBody))
            }
            return quotesApi.getResults(q = "$city, $state, $country")
        }

        return result
    }

    suspend fun searchCounty(county: String, state: String, country: String): APIResponse {
        val quotesApi = RetrofitHelper.getInstance().create(NominatimApi::class.java)
        return quotesApi.getResultsCounty(county = county, state = state, country = country)
    }

    suspend fun searchState(state: String, country: String): APIResponse {
        val quotesApi = RetrofitHelper.getInstance().create(NominatimApi::class.java)
        return quotesApi.getResultsState(state = state, country = country)
    }

    suspend fun searchCountry(country: String): APIResponse {
        val quotesApi = RetrofitHelper.getInstance().create(NominatimApi::class.java)
        return quotesApi.getResultsCountry(country = country)
    }
}