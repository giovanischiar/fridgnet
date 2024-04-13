package io.schiar.fridgnet.library.room.relationentity

import androidx.room.Embedded
import androidx.room.Relation
import io.schiar.fridgnet.library.room.entity.PolygonEntity
import io.schiar.fridgnet.library.room.entity.RegionEntity

/**
 * This class represents the combined result of retrieving a Region entity along with its associated
 * polygon and holes data.

 * @property regionEntity The region data retrieved from the database.
 * @property polygon The main polygon entity of the region, including its geolocation data.
 * @property holes A list of polygon entities representing holes within the main polygon
 * (one-to-many relationship).
 */
data class RegionWithPolygonAndHoles(
    @Embedded
    val regionEntity: RegionEntity,
    @Relation(entity = PolygonEntity::class, parentColumn = "polygonID", entityColumn = "id")
    val polygon: PolygonWithGeoLocations,
    @Relation(entity = PolygonEntity::class, parentColumn = "id", entityColumn = "holesID")
    val holes: List<PolygonWithGeoLocations>
)