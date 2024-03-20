package io.schiar.fridgnet.library.room.relationentity

import androidx.room.Embedded
import androidx.room.Relation
import io.schiar.fridgnet.library.room.entity.AdministrativeUnitEntity
import io.schiar.fridgnet.library.room.entity.CartographicBoundaryEntity

data class AdministrativeUnitWithCartographicBoundaries(
    @Embedded
    val administrativeUnitEntity: AdministrativeUnitEntity,
    @Relation(
        entity = CartographicBoundaryEntity::class,
        parentColumn = "id",
        entityColumn = "administrativeUnitCartographicBoundariesID"
    )
    val cartographicBoundaryEntities: List<CartographicBoundaryWithRegions>
)
