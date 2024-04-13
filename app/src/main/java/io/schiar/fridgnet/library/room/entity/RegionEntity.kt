package io.schiar.fridgnet.library.room.entity

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * The class representation of the entity 'Region' from the database.
 * This entity stores information about a geographic region defined by a polygon.
 *
 * @property id (Long, auto-generated) The unique identifier for the image within the database.
 * @property regionsID the id used to identify in a list of regions in the Cartographic Boundary
 * Entity
 * @property polygonID (Long) The foreign key referencing the `PolygonEntity` that defines the outer
 * boundary of this region.
 * @property active      True if the region is currently displayed on the map, false otherwise
 * (defaults to true).
 * @property boundingBoxSouthwest the Geolocation of the southwest BoundingBox
 * @property boundingBoxNortheast the Geolocation of the northeast BoundingBox
 * @property zIndex (Float) The layer order on the map where the region should be drawn
 * (higher zIndex means drawn on top of others).
 */
@Entity(tableName = "Region")
data class RegionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val regionsID: Long,
    val polygonID: Long,
    val active: Boolean,
    @Embedded(prefix = "boundingBoxSouthwest_")
    var boundingBoxSouthwest: GeoLocationEntity,
    @Embedded(prefix = "boundingBoxNortheast_")
    var boundingBoxNortheast: GeoLocationEntity,
    val zIndex: Float
)