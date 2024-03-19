package io.schiar.fridgnet.library.room.relationentity

import androidx.room.Embedded
import androidx.room.Relation
import io.schiar.fridgnet.library.room.entity.AdministrativeUnitEntity
import io.schiar.fridgnet.library.room.entity.LocationEntity
import io.schiar.fridgnet.library.room.entity.RegionEntity

data class LocationWithRegions(
    @Embedded
    val locationEntity: LocationEntity,
    @Relation(
        entity = AdministrativeUnitEntity::class,
        parentColumn = "administrativeUnitLocationsID",
        entityColumn = "id"
    )
    val administrativeUnitEntity: AdministrativeUnitEntity,
    @Relation(entity = RegionEntity::class, parentColumn = "id", entityColumn = "regionsID")
    val regionEntities: List<RegionWithPolygonAndHoles>
)