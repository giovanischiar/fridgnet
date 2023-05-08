package io.schiar.fridgnet.view.util

import android.util.Log
import com.google.gson.GsonBuilder
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.reflect.TypeToken
import io.schiar.fridgnet.model.Address
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import java.lang.reflect.Type

abstract class GeoJsonAttributes

open class GeoJson<T>(
    val type: String,
    val coordinates: T
)

open class Result<T>(
    val place_id: Int,
    val licence: String,
    val osm_type: String,
    val osm_id: Int,
    val boundingbox: List<String>,
    val lat: String,
    val lon: String,
    val display_name: String,
    val place_rank: Int,
    val category: String,
    val type: String,
    val importance: Double,
    val icon: String,
    val geojson: GeoJson<T>
)

val pointResource = object : TypeToken<Result<List<Double>>?>() {}.type
val lineStringResource = object : TypeToken<Result<List<List<Double>>>?>() {}.type
val polygonResource = object : TypeToken<Result<List<List<List<Double>>>>?>() {}.type
val multiPolygonResource = object : TypeToken<Result<List<List<List<List<Double>>>>>?>() {}.type
val resultDocument = object : TypeToken<Result<GeoJsonAttributes>?>() {}.type

class ResultResourceDeserializer : JsonDeserializer<Result<GeoJsonAttributes>> {
    override fun deserialize(json: JsonElement?, typeOfT: Type?, context: JsonDeserializationContext?): Result<GeoJsonAttributes>? {
        return when (json?.asJsonObject?.get("geojson")?.asJsonObject?.get("type")?.asString) {
            "Point" -> context?.deserialize(json, pointResource)
            "LineString" -> context?.deserialize(json, lineStringResource)
            "Polygon" -> context?.deserialize(json, polygonResource)
            "MultiPolygon" -> context?.deserialize(json, multiPolygonResource)
            else -> null
        }
    }
}

interface NominatimApi {
    @GET("/search?")
    suspend fun getResults(
        @Query("q") q: String,
        @Query("polygon_geojson") polygon_geojson: Int = 1,
        @Query("limit") limit: Int = 1,
        @Query("format") format : String = "jsonv2"
    ) : Response<List<Result<GeoJsonAttributes>>>

    @GET("/search?")
    suspend fun getResultsCity(
        @Query("city") city: String,
        @Query("state") state: String,
        @Query("country") country: String,
        @Query("polygon_geojson") polygon_geojson: Int = 1,
        @Query("limit") limit: Int = 1,
        @Query("format") format : String = "jsonv2"
    ) : Response<List<Result<GeoJsonAttributes>>>
}

object RetrofitHelper {
    private const val baseUrl = "https://nominatim.openstreetmap.org/"

    fun getInstance(): Retrofit {
        val gson = GsonBuilder().apply {
            registerTypeAdapter(resultDocument, ResultResourceDeserializer())
        }.create()
        return Retrofit.Builder().baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }
}

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