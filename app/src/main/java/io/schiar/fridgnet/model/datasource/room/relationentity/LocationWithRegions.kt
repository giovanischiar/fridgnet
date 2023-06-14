package io.schiar.fridgnet.model.datasource.room.relationentity

import androidx.room.Embedded
import androidx.room.Relation
import io.schiar.fridgnet.model.datasource.room.entity.LocationEntity
import io.schiar.fridgnet.model.datasource.room.entity.RegionEntity

data class LocationWithRegions(
    @Embedded
    val locationEntity: LocationEntity,
    @Relation(entity = RegionEntity::class, parentColumn = "id", entityColumn = "regionsID")
    val regions: List<RegionWithPolygonAndHoles>
)