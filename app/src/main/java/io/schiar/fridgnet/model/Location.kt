package io.schiar.fridgnet.model

interface Location {
    val region: List<*>
    val boundingBox: BoundingBox
}
data class LineStringLocation(
    override val region: List<Coordinate>,
    override val boundingBox: BoundingBox
): Location

data class PolygonLocation(
    override val region: List<List<Coordinate>>,
    override val boundingBox: BoundingBox
): Location

data class MultiPolygonLocation(
    override val region: List<List<List<Coordinate>>>,
    override val boundingBox: BoundingBox
): Location

data class BoundingBox(val ne: Coordinate, val sw: Coordinate)