package io.schiar.fridgnet.library.room.relationentity

import androidx.room.Embedded
import androidx.room.Relation
import io.schiar.fridgnet.library.room.entity.GeoLocationEntity
import io.schiar.fridgnet.library.room.entity.PolygonEntity

/**
 * This class represents the combined result of retrieving a Polygon entity along with its
 * associated geolocation data.

 * @property polygon             The polygon data retrieved from the database.
 * @property getLocationEntities A list of GeoLocation entities associated with the polygon
 * (one-to-many relationship).
 */
data class PolygonWithGeoLocations(
    @Embedded
    val polygon: PolygonEntity,
    @Relation(parentColumn = "id", entityColumn = "geoLocationsID")
    val getLocationEntities: List<GeoLocationEntity>
)