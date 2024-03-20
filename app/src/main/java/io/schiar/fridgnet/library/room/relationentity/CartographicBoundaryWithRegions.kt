package io.schiar.fridgnet.library.room.relationentity

import androidx.room.Embedded
import androidx.room.Relation
import io.schiar.fridgnet.library.room.entity.AdministrativeUnitNameEntity
import io.schiar.fridgnet.library.room.entity.CartographicBoundaryEntity
import io.schiar.fridgnet.library.room.entity.RegionEntity

data class CartographicBoundaryWithRegions(
    @Embedded
    val cartographicBoundaryEntity: CartographicBoundaryEntity,
    @Relation(
        entity = AdministrativeUnitNameEntity::class,
        parentColumn = "administrativeUnitNameCartographicBoundariesID",
        entityColumn = "id"
    )
    val administrativeUnitNameEntity: AdministrativeUnitNameEntity,
    @Relation(entity = RegionEntity::class, parentColumn = "id", entityColumn = "regionsID")
    val regionEntities: List<RegionWithPolygonAndHoles>
)