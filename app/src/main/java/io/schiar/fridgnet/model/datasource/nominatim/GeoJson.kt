package io.schiar.fridgnet.model.datasource.nominatim

open class GeoJson<T>(
    val type: String,
    val coordinates: T
)