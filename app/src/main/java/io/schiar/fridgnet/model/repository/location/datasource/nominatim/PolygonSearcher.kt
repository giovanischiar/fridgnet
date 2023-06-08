package io.schiar.fridgnet.model.repository.location.datasource.nominatim

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
        return quotesApi.getResultsCity(city = city, state = state, country = country)
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