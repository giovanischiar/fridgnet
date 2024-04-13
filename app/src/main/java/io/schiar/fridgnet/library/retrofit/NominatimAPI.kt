package io.schiar.fridgnet.library.retrofit

import retrofit2.http.GET
import retrofit2.http.Query

/**
 * The interface for interacting with the Nominatim API to fetch information required for building
 * `CartographicBoundary` objects.
 * This interface defines methods for retrieving geospatial data based on different administrative
 * unit types.
 */
interface NominatimAPI {
    /**
     * this one is more generic. It's used when the more specific one didn't return a proper Polygon
     * @param q the full name of the administrative unit, typically using the
     * [AdministrativeUnitName]'s toString
     *
     * @param polygonGeoJSON speficies the API to return the geoJSON format
     * @param limit limits the result of only one
     * @param format speficies that will be a json return
     *
     * @return a [List] of results for the search, as it's limited to 1 the result would be only one
     */
    @GET("/search?")
    suspend fun getResults(
        @Query("q") q: String,
        @Query("polygon_geojson") polygonGeoJSON: Int = 1,
        @Query("limit") limit: Int = 1,
        @Query("format") format: String = "jsonv2"
    ): List<JSONResult<GeoJSONAttributes>>


    /**
     * Fetches the results for a specific city using the Nominatim API.
     *
     * @param city The name of the city (typically from the `locality` property of
     * `AdministrativeUnitName`).
     * @param state The state name (typically from the `adminArea` property of
     * `AdministrativeUnitName`).
     * @param country The country name (typically from the `countryName` property of
     * `AdministrativeUnitName`).
     * @param polygonGeoJSON specifies the API to return the geoJSON format
     * @param limit limits the result of 2 to check if the second one is more relevant
     * @param format specifies that will be a json return
     *
     * @return A list of JSON results from the Nominatim API (limited to 2). The first
     * result is typically chosen, but if the second has a higher relevancy score
     * (based on certain criteria, e.g., specific property value), it might be used instead.
     */
    @GET("/search?")
    suspend fun getResultsCity(
        @Query("city") city: String,
        @Query("state") state: String,
        @Query("country") country: String,
        @Query("polygon_geojson") polygonGeoJSON: Int = 1,
        @Query("limit") limit: Int = 2,
        @Query("format") format: String = "jsonv2"
    ): List<JSONResult<GeoJSONAttributes>>

    /**
     * Fetches the results for a specific county using the Nominatim API.
     *
     * @param county The county name (typically from the `subAdminArea` property of
     * `AdministrativeUnitName`).
     * @param state The state name (typically from the `adminArea` property of
     * `AdministrativeUnitName`).
     * @param country The country name (typically from the `countryName` property of
     * `AdministrativeUnitName`).
     * @param polygonGeoJSON speficies the API to return the geoJSON format
     * @param limit limits the result of only one
     * @param format speficies that will be a json return
     *
     * @return a [List] of results for the search, as it's limited to 1 the result would be only one
     */
    @GET("/search?")
    suspend fun getResultsCounty(
        @Query("county") county: String,
        @Query("state") state: String,
        @Query("country") country: String,
        @Query("polygon_geojson") polygonGeoJSON: Int = 1,
        @Query("limit") limit: Int = 1,
        @Query("format") format: String = "jsonv2"
    ): List<JSONResult<GeoJSONAttributes>>


    /**
     * Fetches the results for a specific state using the Nominatim API.
     *
     * @param state The state name (typically from the `adminArea` property of
     * `AdministrativeUnitName`).
     * @param country The country name (typically from the `countryName` property of
     * `AdministrativeUnitName`).
     * @param polygonGeoJSON specifies the API to return the geoJSON format
     * @param limit limits the result of only one
     * @param format specifies that will be a json return
     *
     * @return a [List] of results for the search, as it's limited to 1 the result would be only one
     */
    @GET("/search?")
    suspend fun getResultsState(
        @Query("state") state: String,
        @Query("country") country: String,
        @Query("polygon_geojson") polygonGeoJSON: Int = 1,
        @Query("limit") limit: Int = 1,
        @Query("format") format: String = "jsonv2"
    ): List<JSONResult<GeoJSONAttributes>>

    /**
     * Fetches the results for a specific country using the Nominatim API.
     *
     * @param country The country name (typically from the `countryName` property of
     * `AdministrativeUnitName`).
     * @param polygonGeoJSON speficies the API to return the geoJSON format
     * @param limit limits the result of only one
     * @param format speficies that will be a json return
     *
     * @return a [List] of results for the search, as it's limited to 1 the result would be only one
     */
    @GET("/search?")
    suspend fun getResultsCountry(
        @Query("country") country: String,
        @Query("polygon_geojson") polygonGeoJSON: Int = 1,
        @Query("limit") limit: Int = 1,
        @Query("format") format: String = "jsonv2"
    ): List<JSONResult<GeoJSONAttributes>>
}