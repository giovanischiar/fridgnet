package io.schiar.fridgnet.library.room

import io.schiar.fridgnet.library.room.entity.RegionEntity
import io.schiar.fridgnet.model.Region

fun Region.toRegionEntity(regionsID: Long, polygonID: Long): RegionEntity {
    return RegionEntity(
        regionsID = regionsID,
        polygonID = polygonID,
        active = active,
        boundingBoxSouthwest = boundingBox.southwest.toCoordinateEntity(),
        boundingBoxNortheast = boundingBox.northeast.toCoordinateEntity(),
        zIndex = zIndex,
    )
}