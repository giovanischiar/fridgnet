package io.schiar.fridgnet.model.datasource.nominatim

import io.schiar.fridgnet.Log
import io.schiar.fridgnet.model.Address
import retrofit2.Response

class PolygonSearcher() {
    suspend fun search(address: Address): Response<List<Result<GeoJsonAttributes>>> {
        val quotesApi = RetrofitHelper.getInstance().create(NominatimApi::class.java)
        return if(
            address.locality != null &&
            address.subAdminArea != null &&
            address.adminArea != null &&
            address.countryName != null
        ) {
            quotesApi.getResultsCity(
                city = address.locality,
                state = address.adminArea,
                country = address.countryName
            )
        } else {
            val name = address.name()
            Log.d("api result", "searching $name")
            quotesApi.getResults(q = name)
        }
    }

    suspend fun searchCity(city: String, state: String, country: String): Response<List<Result<GeoJsonAttributes>>> {
        val quotesApi = RetrofitHelper.getInstance().create(NominatimApi::class.java)
        val result = quotesApi.getResultsCity(city = city, state = state, country = country)
        val bodies = result.body() ?: return result
        val body = if (bodies.isEmpty()) return result else bodies[0]
        val geojson = body.geojson
        val type = geojson.type

        if (type == "Polygon") {
            if (bodies.size == 1) return result
            val secondBody = bodies[1]
            if (secondBody.display_name == body.display_name && secondBody.type == "administrative") {
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
            Log.d("Search for API Polygon", "Second Result name: {$secondBody.name} type: {$secondBody.type}")
            if (secondBody.display_name == body.display_name && secondBody.type == "administrative") {
                Log.d("Search for API Polygon", "Second body is the administrative")
                return Response.success(listOf(secondBody))
            }
            return quotesApi.getResults(q = "$city, $state, $country")
        }

        return result
    }

    suspend fun searchCounty(county: String, state: String, country: String): Response<List<Result<GeoJsonAttributes>>> {
        val quotesApi = RetrofitHelper.getInstance().create(NominatimApi::class.java)
        return quotesApi.getResultsCounty(county = county, state = state, country = country)
    }

    suspend fun searchState(state: String, country: String): Response<List<Result<GeoJsonAttributes>>> {
        val quotesApi = RetrofitHelper.getInstance().create(NominatimApi::class.java)
        return quotesApi.getResultsState(state = state, country = country)
    }

    suspend fun searchCountry(country: String): Response<List<Result<GeoJsonAttributes>>> {
        val quotesApi = RetrofitHelper.getInstance().create(NominatimApi::class.java)
        return quotesApi.getResultsCountry(country = country)
    }
}