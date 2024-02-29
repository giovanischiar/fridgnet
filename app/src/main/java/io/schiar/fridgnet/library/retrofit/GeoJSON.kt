package io.schiar.fridgnet.library.retrofit

open class GeoJSON<T>(
    val type: String,
    val coordinates: T
)