package io.schiar.fridgnet.library.room.entity

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * The class representation of the entity 'CartographicBoundary' from the database. This entity
 * stores information about cartographic boundaries associated with administrative units.
 *
 * @property id (Long, auto-generated) the unique identifier for the cartographic boundary within
 * the database.
 * @property administrativeUnitNameCartographicBoundariesID the id of the AdministrativeUnitName
 * entity. When the AdministrativeUnitName will be retrieved from the database the
 * CartographicBoundary entity associated with it will come together
 * @property administrativeLevel the administrative level of this unit (e.g., state, county, city).
 * @param boundingBoxSouthwest the Geolocation of the southwest BoundingBox.
 * @param boundingBoxNortheast the Geolocation of the northeast BoundingBox.
 * @property zIndex the layer order on the map where the boundary should be drawn (higher zIndex
 * means drawn on top of others).
 */
@Entity(tableName = "CartographicBoundary")
data class CartographicBoundaryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val administrativeUnitNameCartographicBoundariesID: Long,
    val administrativeLevel: String,
    @Embedded(prefix = "boundingBoxSouthwest_")
    var boundingBoxSouthwest: GeoLocationEntity,
    @Embedded(prefix = "boundingBoxNortheast_")
    var boundingBoxNortheast: GeoLocationEntity,
    val zIndex: Float
)