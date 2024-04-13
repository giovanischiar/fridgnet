package io.schiar.fridgnet.library.room.relationentity

import androidx.room.Embedded
import androidx.room.Relation
import io.schiar.fridgnet.library.room.entity.AdministrativeUnitNameEntity
import io.schiar.fridgnet.library.room.entity.CartographicBoundaryEntity
import io.schiar.fridgnet.library.room.entity.RegionEntity

/**
 * This class represents the combined result of retrieving a CartographicBoundary entity along with
 * its associated AdministrativeUnitName entity and all its RegionWithPolygonAndHoles entities.

 * @property cartographicBoundaryEntity The cartographic boundary information retrieved from the
 * database.
 * @property administrativeUnitNameEntity The administrative unit name (e.g., city, county, state)
 * associated with the cartographic boundary (one-to-one relationship).
 * @property regionEntities A [List] of [RegionWithPolygonAndHoles]
 * entities representing the regions within the cartographic boundary (one-to-many relationship).
 */
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