package io.schiar.fridgnet.model.repository.datasource.room.entitywithlist

import androidx.room.Embedded
import androidx.room.Relation
import io.schiar.fridgnet.model.BoundingBox
import io.schiar.fridgnet.model.Region
import io.schiar.fridgnet.model.repository.datasource.room.entity.PolygonEntity
import io.schiar.fridgnet.model.repository.datasource.room.entity.RegionEntity

data class RegionWithPolygonAndHoles(
    @Embedded
    val regionEntity: RegionEntity,
    @Relation(entity = PolygonEntity::class, parentColumn = "polygonID", entityColumn = "id")
    val polygon: PolygonWithCoordinates,
    @Relation(entity = PolygonEntity::class, parentColumn = "id", entityColumn = "holesID")
    val holes: List<PolygonWithCoordinates>
) {
    fun toRegion(): Region {
        return Region(
            polygon = polygon.toPolygon(),
            holes = holes.map { it.toPolygon() },
            active = regionEntity.active,
            boundingBox = BoundingBox(
                southwest = regionEntity.boundingBoxSouthwest.toCoordinate(),
                northeast = regionEntity.boundingBoxNortheast.toCoordinate(),
            ),
            zIndex = regionEntity.zIndex
        )
    }
}
