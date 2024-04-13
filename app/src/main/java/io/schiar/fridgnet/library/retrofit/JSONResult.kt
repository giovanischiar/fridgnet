package io.schiar.fridgnet.library.retrofit

import com.google.gson.annotations.SerializedName

/**
 * The class that represents the JSON returned by the API.

 * @property boundingBox A list of four strings representing the bounding box coordinates in the
 * order: minimum longitude, minimum latitude, maximum longitude, maximum latitude.
 * @property geoJSON The GeoJSON object containing the coordinates of the location.
 * @property displayName The display name for the location as provided by the API.
 * @property type A string indicating the type of feature returned by the API
 */

open class JSONResult<T>(
    @SerializedName("boundingbox") val boundingBox: List<String>,
    @SerializedName("geojson") val geoJSON: GeoJSON<T>,
    @SerializedName("display_name") val displayName: String,
    val type: String
)