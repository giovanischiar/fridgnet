package io.schiar.fridgnet.model.repository.location.datasource.room.relationentity

import androidx.room.Embedded
import androidx.room.Relation
import io.schiar.fridgnet.model.repository.location.datasource.room.entity.PolygonEntity
import io.schiar.fridgnet.model.repository.location.datasource.room.entity.RegionEntity

data class RegionWithPolygonAndHoles(
    @Embedded
    val regionEntity: RegionEntity,
    @Relation(entity = PolygonEntity::class, parentColumn = "polygonID", entityColumn = "id")
    val polygon: PolygonWithCoordinates,
    @Relation(entity = PolygonEntity::class, parentColumn = "id", entityColumn = "holesID")
    val holes: List<PolygonWithCoordinates>
)
