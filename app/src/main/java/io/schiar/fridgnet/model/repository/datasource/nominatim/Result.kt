package io.schiar.fridgnet.model.repository.datasource.nominatim

open class Result<T>(
    val boundingbox: List<String>,
    val geojson: GeoJson<T>
)