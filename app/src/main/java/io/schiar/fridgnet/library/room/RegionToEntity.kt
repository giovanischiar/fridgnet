package io.schiar.fridgnet.library.room

import io.schiar.fridgnet.library.room.entity.RegionEntity
import io.schiar.fridgnet.model.Region

/**
 * Converts the Region object to a RegionEntity, specifying the foreign key relationships.
 *
 * @param regionsID The ID of the associated Cartographic Boundary entity (foreign key).
 * This likely represents the administrative boundary information for the region.
 * @param polygonID The ID of the associated Polygon entity (foreign key).
 * This likely represents the geometric shape of the region.
 * @return the [RegionEntity] object.
 */
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

/**
 * Converts the Region object to a RegionEntity using a specified id for update purposes, and
 * specifying the foreign key relationships.
 *
 * @param id the id used to update the alreary existent entity
 * @param regionsID The ID of the associated Cartographic Boundary entity (foreign key).
 * This likely represents the administrative boundary information for the region.
 * @param polygonID The ID of the associated Polygon entity (foreign key).
 * This likely represents the geometric shape of the region.
 * @return the [RegionEntity] object.
 */
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