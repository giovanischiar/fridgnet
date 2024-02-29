package io.schiar.fridgnet.library.retrofit

import com.google.gson.annotations.SerializedName

open class JSONResult<T>(
    @SerializedName("boundingbox") val boundingBox: List<String>,
    @SerializedName("geojson") val geoJSON: GeoJSON<T>,
    @SerializedName("display_name") val displayName: String,
    val type: String
)