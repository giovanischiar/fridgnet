package io.schiar.fridgnet.model.datasource.nominatim

open class Result<T>(
    val boundingbox: List<String>,
    val geojson: GeoJson<T>,
    val display_name: String,
    val type: String
)