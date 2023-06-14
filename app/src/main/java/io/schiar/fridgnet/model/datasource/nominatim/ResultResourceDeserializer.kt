package io.schiar.fridgnet.model.datasource.nominatim

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type

class ResultResourceDeserializer : JsonDeserializer<Result<io.schiar.fridgnet.model.datasource.nominatim.GeoJsonAttributes>> {
    private val pointResource = object : TypeToken<Result<List<Double>>?>() {}.type
    private val lineStringResource = object : TypeToken<Result<List<List<Double>>>?>() {}.type
    private val polygonResource = object : TypeToken<Result<List<List<List<Double>>>>?>() {}.type
    private val multiPolygonResource = object : TypeToken<Result<List<List<List<List<Double>>>>>?>() {}.type

    override fun deserialize(json: JsonElement?, typeOfT: Type?, context: JsonDeserializationContext?): Result<io.schiar.fridgnet.model.datasource.nominatim.GeoJsonAttributes>? {
        return when (json?.asJsonObject?.get("geojson")?.asJsonObject?.get("type")?.asString) {
            "Point" -> context?.deserialize(json, pointResource)
            "LineString" -> context?.deserialize(json, lineStringResource)
            "Polygon" -> context?.deserialize(json, polygonResource)
            "MultiPolygon" -> context?.deserialize(json, multiPolygonResource)
            else -> null
        }
    }
}