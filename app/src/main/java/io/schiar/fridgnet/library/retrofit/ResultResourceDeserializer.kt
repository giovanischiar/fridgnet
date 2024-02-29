package io.schiar.fridgnet.library.retrofit

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type

class ResultResourceDeserializer : JsonDeserializer<JSONResult<GeoJSONAttributes>> {
    private val pointResource = object : TypeToken<JSONResult<List<Double>>?>() {}.type
    private val lineStringResource = object : TypeToken<JSONResult<List<List<Double>>>?>() {}.type
    private val polygonResource = object : TypeToken<JSONResult<List<List<List<Double>>>>?>() {}.type
    private val multiPolygonResource =
        object : TypeToken<JSONResult<List<List<List<List<Double>>>>>?>() {}.type

    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): JSONResult<GeoJSONAttributes>? {
        return when (json?.asJsonObject?.get("geojson")?.asJsonObject?.get("type")?.asString) {
            "Point" -> context?.deserialize(json, pointResource)
            "LineString" -> context?.deserialize(json, lineStringResource)
            "Polygon" -> context?.deserialize(json, polygonResource)
            "MultiPolygon" -> context?.deserialize(json, multiPolygonResource)
            else -> null
        }
    }
}