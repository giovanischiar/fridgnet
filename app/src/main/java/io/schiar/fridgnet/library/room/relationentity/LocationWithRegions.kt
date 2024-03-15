package io.schiar.fridgnet.library.room.relationentity

import androidx.room.Embedded
import androidx.room.Relation
import io.schiar.fridgnet.library.room.entity.AddressEntity
import io.schiar.fridgnet.library.room.entity.LocationEntity
import io.schiar.fridgnet.library.room.entity.RegionEntity

data class LocationWithRegions(
    @Embedded
    val locationEntity: LocationEntity,
    @Relation(
        entity = AddressEntity::class,
        parentColumn = "addressLocationsID",
        entityColumn = "id"
    )
    val addressEntity: AddressEntity,
    @Relation(entity = RegionEntity::class, parentColumn = "id", entityColumn = "regionsID")
    val regions: List<RegionWithPolygonAndHoles>
)