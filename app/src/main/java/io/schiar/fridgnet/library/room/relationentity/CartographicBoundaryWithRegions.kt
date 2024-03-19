package io.schiar.fridgnet.library.room.relationentity

import androidx.room.Embedded
import androidx.room.Relation
import io.schiar.fridgnet.library.room.entity.AdministrativeUnitEntity
import io.schiar.fridgnet.library.room.entity.CartographicBoundaryEntity
import io.schiar.fridgnet.library.room.entity.RegionEntity

data class CartographicBoundaryWithRegions(
    @Embedded
    val cartographicBoundaryEntity: CartographicBoundaryEntity,
    @Relation(
        entity = AdministrativeUnitEntity::class,
        parentColumn = "administrativeUnitCartographicBoundariesID",
        entityColumn = "id"
    )
    val administrativeUnitEntity: AdministrativeUnitEntity,
    @Relation(entity = RegionEntity::class, parentColumn = "id", entityColumn = "regionsID")
    val regionEntities: List<RegionWithPolygonAndHoles>
)