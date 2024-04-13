package io.schiar.fridgnet.model

/**
 * Represents a region on a map.
 *
 * @property id          The database ID of the region (defaults to 0).
 * @property polygon     The outermost Polygon object defining the boundary of the region.
 * @property holes       A list of Polygon objects representing interior holes that define areas
 * excluded from the region.
 * @property active      True if the region is currently displayed on the map, false otherwise
 * (defaults to true).
 * @property boundingBox The BoundingBox object encompassing the entire region (including holes).
 * @property zIndex      The layer order on the map where the region should be drawn (higher zIndex
 * means drawn on top of others).
 */
data class Region(
    val id: Long = 0,
    val polygon: Polygon,
    val holes: List<Polygon>,
    val active: Boolean = true,
    val boundingBox: BoundingBox,
    val zIndex: Float
) {
    /**
     * @return the region switched which means the region could turn visible in the map or not.
     */
    fun switch(): Region {
        return Region(id, polygon, holes, !active, boundingBox, zIndex)
    }
}