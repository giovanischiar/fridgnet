package io.schiar.fridgnet.library.room

import io.schiar.fridgnet.library.room.entity.RegionEntity
import io.schiar.fridgnet.model.Region

fun Region.toRegionEntity(regionsID: Long, polygonID: Long): RegionEntity {
    return RegionEntity(
        regionsID = regionsID,
        polygonID = polygonID,
        active = active,
        boundingBoxSouthwest = boundingBox.southwest.toGeoLocationEntity(),
        boundingBoxNortheast = boundingBox.northeast.toGeoLocationEntity(),
        zIndex = zIndex,
    )
}

fun Region.toRegionEntity(id: Long, regionsID: Long, polygonID: Long): RegionEntity {
    return RegionEntity(
        id = id,
        regionsID = regionsID,
        polygonID = polygonID,
        active = active,
        boundingBoxSouthwest = boundingBox.southwest.toGeoLocationEntity(),
        boundingBoxNortheast = boundingBox.northeast.toGeoLocationEntity(),
        zIndex = zIndex,
    )
}