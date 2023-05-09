package io.schiar.fridgnet.model.nominatim

open class GeoJson<T>(
    val type: String,
    val coordinates: T
)