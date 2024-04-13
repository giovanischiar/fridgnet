package io.schiar.fridgnet.library.room

import io.schiar.fridgnet.library.room.entity.GeoLocationEntity
import io.schiar.fridgnet.model.GeoLocation

/**
 * Converts the [GeoLocation] into the [GeoLocationEntity] with an association to an administrative
 * unit name.
 *
 * This function creates a new GeoLocationEntity with the provided GeoLocation data and sets the
 * administrativeUnitNameGeoLocationsID as a foreign key value. This likely associates the
 * GeoLocationEntity with a specific administrative unit name record in the database.
 *
 * @param administrativeUnitNameGeoLocationsID the ID of the administrative unit name to associate
 * with this [GeoLocationEntity].
 * @return the converted [GeoLocationEntity] object.
 */
fun GeoLocation.toGeoLocationEntity(administrativeUnitNameGeoLocationsID: Long): GeoLocationEntity {
    return GeoLocationEntity(
        id = id,
        administrativeUnitNameGeoLocationsID = administrativeUnitNameGeoLocationsID,
        latitude = latitude,
        longitude = longitude
    )
}

/**
 * Converts the [GeoLocation] into the [GeoLocationEntity], optionally associating it with an image.
 *
 * This function creates a new [GeoLocationEntity] with the provided GeoLocation data. The
 * `geoLocationsID` parameter is optional and can be used to set a foreign key value. This would
 * associate the GeoLocationEntity with a specific image record in the database (if provided).
 *
 * @param geoLocationsID (optional) the ID of the image to associate with this GeoLocationEntity.
 * @return the converted GeoLocationEntity object.
 */
fun GeoLocation.toGeoLocationEntity(geoLocationsID: Long? = null): GeoLocationEntity {
    return GeoLocationEntity(
        geoLocationsID = geoLocationsID,
        latitude = latitude,
        longitude = longitude
    )
}

/**
 * Converts the [GeoLocation] into the [GeoLocationEntity], optionally associating it with an image,
 * and using a provided ID.
 *
 * This function creates a new GeoLocationEntity with the provided GeoLocation data and sets the
 * `id` field. The `id` parameter is typically used when updating existing records in the database
 * and should be set to the ID retrieved from the database query.
 *
 * The `geoLocationsID` parameter (optional) can be used to set a foreign key value. This would
 * associate the GeoLocationEntity with a specific image record in the database (if provided).
 *
 * @param id the ID to be used in the GeoLocationEntity, typically for updates based on database
 * retrievals.
 * @param geoLocationsID (optional) the ID of the image to associate with this GeoLocationEntity.
 * @return the converted GeoLocationEntity object.
 */
fun GeoLocation.toGeoLocationEntity(id: Long, geoLocationsID: Long? = null): GeoLocationEntity {
    return GeoLocationEntity(
        id = id,
        geoLocationsID = geoLocationsID,
        latitude = latitude,
        longitude = longitude
    )
}