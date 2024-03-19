package io.schiar.fridgnet.library.room.relationentity

import androidx.room.Embedded
import androidx.room.Relation
import io.schiar.fridgnet.library.room.entity.PolygonEntity
import io.schiar.fridgnet.library.room.entity.RegionEntity

data class RegionWithPolygonAndHoles(
    @Embedded
    val regionEntity: RegionEntity,
    @Relation(entity = PolygonEntity::class, parentColumn = "polygonID", entityColumn = "id")
    val polygon: PolygonWithGeoLocations,
    @Relation(entity = PolygonEntity::class, parentColumn = "id", entityColumn = "holesID")
    val holes: List<PolygonWithGeoLocations>
)
