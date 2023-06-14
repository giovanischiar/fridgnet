package io.schiar.fridgnet.model.datasource.room.relationentity

import androidx.room.Embedded
import androidx.room.Relation
import io.schiar.fridgnet.model.datasource.room.entity.CoordinateEntity
import io.schiar.fridgnet.model.datasource.room.entity.PolygonEntity

data class PolygonWithCoordinates(
    @Embedded
    val polygon: PolygonEntity,
    @Relation(parentColumn = "id", entityColumn = "coordinatesID")
    val coordinates: List<CoordinateEntity>
)