package io.schiar.fridgnet.library.retrofit

import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitHelper {
    private const val baseUrl = "https://nominatim.openstreetmap.org/"
    private val resultDocument = object : TypeToken<JSONResult<GeoJSONAttributes>?>() {}.type

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