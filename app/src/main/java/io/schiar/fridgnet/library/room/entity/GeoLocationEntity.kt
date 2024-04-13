package io.schiar.fridgnet.library.room.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * The class representation of the entity 'GeoLocation' from the database.
 * This entity stores the geographic location (latitude and longitude) data.
 *
 * @property id (Long, auto-generated) The unique identifier for the geolocation within the
 * database.
 * @property geoLocationsID (Long, nullable) Currently unused, reserved for potential future use
 * with image locations.
 * @property administrativeUnitNameGeoLocationsID (Long, nullable) The ID referencing an associated
 * AdministrativeUnitName entity (optional).
 * @property latitude (Double) The y-axis coordinate of the location in degrees, ranging from -90
 * (South Pole) to 90 (North Pole).
 * @property longitude (Double) The x-axis coordinate of the location in degrees, ranging from -180
 * (West) to 180 (East).
 */
@Entity(tableName = "GeoLocation")
data class GeoLocationEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val geoLocationsID: Long? = null,
    val administrativeUnitNameGeoLocationsID: Long? = null,
    val latitude: Double,
    val longitude: Double
)