package io.schiar.fridgnet.library.room.relationentity

import androidx.room.Embedded
import androidx.room.Relation
import io.schiar.fridgnet.library.room.entity.AdministrativeUnitNameEntity
import io.schiar.fridgnet.library.room.entity.CartographicBoundaryEntity

data class AdministrativeUnitNameWithCartographicBoundaries(
    @Embedded
    val administrativeUnitNameEntity: AdministrativeUnitNameEntity,
    @Relation(
        entity = CartographicBoundaryEntity::class,
        parentColumn = "id",
        entityColumn = "administrativeUnitNameCartographicBoundariesID"
    )
    val cartographicBoundaryEntities: List<CartographicBoundaryWithRegions>
)
