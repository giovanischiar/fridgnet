package io.schiar.fridgnet.model.nominatim

import android.util.Log
import io.schiar.fridgnet.model.Address
import io.schiar.fridgnet.model.name
import retrofit2.Response

class PolygonSearcher(private val address: Address) {
    suspend fun search(): Response<List<Result<GeoJsonAttributes>>> {
        val quotesApi = RetrofitHelper.getInstance().create(NominatimApi::class.java)
        return if(
            address.locality != null &&
            address.subAdminArea != null &&
            address.adminArea != null &&
            address.countryName != null
        ) {
            Log.d("api result", "searching ${address.name()}")
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
}