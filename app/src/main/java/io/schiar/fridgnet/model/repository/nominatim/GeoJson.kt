package io.schiar.fridgnet.model.repository.nominatim

open class GeoJson<T>(
    val type: String,
    val coordinates: T
)