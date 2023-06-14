package io.schiar.fridgnet.model.datasource.nominatim

open class Result<T>(
    val boundingbox: List<String>,
    val geojson: io.schiar.fridgnet.model.datasource.nominatim.GeoJson<T>
)