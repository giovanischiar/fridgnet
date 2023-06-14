package io.schiar.fridgnet.model.datasource.nominatim

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

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

    @GET("/search?")
    suspend fun getResultsCounty(
        @Query("county") county: String,
        @Query("state") state: String,
        @Query("country") country: String,
        @Query("polygon_geojson") polygon_geojson: Int = 1,
        @Query("limit") limit: Int = 1,
        @Query("format") format : String = "jsonv2"
    ) : Response<List<Result<GeoJsonAttributes>>>

    @GET("/search?")
    suspend fun getResultsState(
        @Query("state") state: String,
        @Query("country") country: String,
        @Query("polygon_geojson") polygon_geojson: Int = 1,
        @Query("limit") limit: Int = 1,
        @Query("format") format : String = "jsonv2"
    ) : Response<List<Result<GeoJsonAttributes>>>

    @GET("/search?")
    suspend fun getResultsCountry(
        @Query("country") country: String,
        @Query("polygon_geojson") polygon_geojson: Int = 1,
        @Query("limit") limit: Int = 1,
        @Query("format") format : String = "jsonv2"
    ) : Response<List<Result<GeoJsonAttributes>>>
}