package io.schiar.fridgnet.library.retrofit

/**
 * geoJSON class used to create objects in the Nominatim API JSON result it follows the guidelines
 * of the GeoJSON format
 *
 * @property type it can be returned by the api as Point, LineString, Polygon and MultiPolygon. This
 * propriety will be important to infer the coordinates T param.
 * @property coordinates the coordinates that will be the type according to the string [type]. It
 * follows the guidelines of (GeoJSON)[https://datatracker.ietf.org/doc/html/rfc7946]
 *
 * - if it's a Point it is a List<Double> size two where first is longitude and second latitude.
 * - if it's a LineString it is a List<List<Double>>
 * - if it's a Polygon it is a List<List<Double>>
 * - if it's a Multipolygon it is a List<List<List<Double>>>
 *
 *  Although only Polygon and Multipolygon are used to plot locations on the map, there were times
 *  when the API returned Point or LineString, making me have to handle those types as well. The
 *  issue was that the coordinates field had a variable type, so I had to learn how to create a
 *  custom JSON deserializer when converting the JSON into Kotlin objects.
 */
open class GeoJSON<T>(val type: String, val coordinates: T)