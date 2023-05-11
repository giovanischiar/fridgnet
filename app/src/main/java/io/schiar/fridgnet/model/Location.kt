package io.schiar.fridgnet.model

data class Location(
    val regions: List<Region>,
    val boundingBox: BoundingBox
)