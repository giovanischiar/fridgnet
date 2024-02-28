package io.schiar.fridgnet.library.nominatim

open class GeoJson<T>(
    val type: String,
    val coordinates: T
)