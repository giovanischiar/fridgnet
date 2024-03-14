package io.schiar.fridgnet.model

data class Region(
    val id: Long = 0,
    val polygon: Polygon,
    val holes: List<Polygon>,
    val active: Boolean = true,
    val boundingBox: BoundingBox,
    val zIndex: Float
) {
    fun switch(): Region {
        return Region(id, polygon, holes, !active, boundingBox, zIndex)
    }
}