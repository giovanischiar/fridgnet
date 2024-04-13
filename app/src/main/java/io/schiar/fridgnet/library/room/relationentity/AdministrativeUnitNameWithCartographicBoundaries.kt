package io.schiar.fridgnet.library.room.relationentity

import androidx.room.Embedded
import androidx.room.Relation
import io.schiar.fridgnet.library.room.entity.AdministrativeUnitNameEntity
import io.schiar.fridgnet.library.room.entity.CartographicBoundaryEntity

/**
 * This class represents the combined result of retrieving an AdministrativeUnitName entity along
 * with all its associated CartographicBoundary entities.
 *
 * @property administrativeUnitNameEntity the [AdministrativeUnitNameEntity]
 * @property cartographicBoundaryEntities A list of [CartographicBoundaryWithRegions] entities
 * associated with the [AdministrativeUnitNameEntity] (one-to-many relationship).
 */
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
