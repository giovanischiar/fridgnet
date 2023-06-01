package io.schiar.fridgnet.model.repository.datasource.room.entitywithlist

import androidx.room.Embedded
import androidx.room.Relation
import io.schiar.fridgnet.model.BoundingBox
import io.schiar.fridgnet.model.Location
import io.schiar.fridgnet.model.repository.datasource.room.entity.LocationEntity
import io.schiar.fridgnet.model.repository.datasource.room.entity.RegionEntity

data class LocationWithRegions(
    @Embedded
    val locationEntity: LocationEntity,
    @Relation(entity = RegionEntity::class, parentColumn = "id", entityColumn = "regionsID")
    val regions: List<RegionWithPolygonAndHoles>
) {
    fun toLocation(): Location {
        return Location(
            address = locationEntity.address,
            administrativeUnit = locationEntity.administrativeUnit,
            regions = regions.map { it.toRegion() },
            boundingBox = BoundingBox(
                southwest = locationEntity.boundingBoxSouthwest.toCoordinate(),
                northeast = locationEntity.boundingBoxNortheast.toCoordinate(),
            ),
            zIndex = locationEntity.zIndex
        )
    }
}