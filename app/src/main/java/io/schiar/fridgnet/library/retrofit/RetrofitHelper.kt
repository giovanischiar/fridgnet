package io.schiar.fridgnet.library.retrofit

import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Helper class for creating a Retrofit instance to interact with the Nominatim API.
 * This class uses a singleton pattern to provide a single instance of the Retrofit object.
 */
object RetrofitHelper {
    private const val baseUrl = "https://nominatim.openstreetmap.org/"
    private val resultDocument = object : TypeToken<JSONResult<GeoJSONAttributes>?>() {}.type

    /**
     * @return a single instance of Retrofit configured to interact with the Nominatim API.
     */
    fun getInstance(): Retrofit {
        val gson = GsonBuilder().apply {
            registerTypeAdapter(resultDocument, ResultResourceDeserializer())
        }.create()
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }
}