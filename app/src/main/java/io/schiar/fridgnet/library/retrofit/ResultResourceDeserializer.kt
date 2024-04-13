package io.schiar.fridgnet.library.retrofit

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type

/**
 * Custom deserializer for `JSONResult<GeoJSONAttributes>` objects handling different GeoJSON types.
 *
 * This deserializer is used with Gson to handle the fact that the JSON response structure for a
 * `JSONResult<GeoJSONAttributes>` object can vary depending on the `type` property within the
 * "geojson" node.
 *
 * This class maps the `type` value to the appropriate deserialization logic.
 */
class ResultResourceDeserializer : JsonDeserializer<JSONResult<GeoJSONAttributes>> {
    private val pointResource = object : TypeToken<JSONResult<List<Double>>?>() {}.type
    private val lineStringResource = object : TypeToken<JSONResult<List<List<Double>>>?>() {}.type
    private val polygonResource = object : TypeToken<
            JSONResult<List<List<List<Double>>>>?
    >() {}.type
    private val multiPolygonResource =
        object : TypeToken<JSONResult<List<List<List<List<Double>>>>>?>() {}.type

    /**
     * Deserializes the JSON element based on the `type` property within the "geojson" node.
     *
     * @param json The JSON element to deserialize.
     * @param typeOfT The expected type of the deserialized object (should be
     * `JSONResult<GeoJSONAttributes>`).
     * @param context The deserialization context from Gson.
     *
     * @return The deserialized `JSONResult<GeoJSONAttributes>` object or null if the type is not
     * supported.
     */
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