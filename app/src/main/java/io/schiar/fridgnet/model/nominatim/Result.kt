package io.schiar.fridgnet.model.nominatim

open class Result<T>(
    val boundingbox: List<String>,
    val geojson: GeoJson<T>
)