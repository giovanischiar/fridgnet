package io.schiar.fridgnet.model

data class Region(
    val polygon: Polygon,
    val holes: List<Polygon>,
    val active: Boolean = true,
    val boundingBox: BoundingBox,
    val zIndex: Float
)