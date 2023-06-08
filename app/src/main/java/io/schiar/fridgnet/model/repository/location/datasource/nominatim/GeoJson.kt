package io.schiar.fridgnet.model.repository.location.datasource.nominatim

open class GeoJson<T>(
    val type: String,
    val coordinates: T
)