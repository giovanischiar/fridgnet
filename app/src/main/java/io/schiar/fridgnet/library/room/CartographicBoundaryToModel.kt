package io.schiar.fridgnet.library.room

import io.schiar.fridgnet.library.room.entity.CartographicBoundaryEntity
import io.schiar.fridgnet.library.room.entity.GeoLocationEntity
import io.schiar.fridgnet.model.CartographicBoundary
import io.schiar.fridgnet.model.GeoLocation

/**
 * Converts a List<GeoLocation> to a List<GeoLocationEntity> with a specified ID for association.
 *
 * This function iterates through the provided List<GeoLocation> and converts each GeoLocation
 * object to a [GeoLocationEntity]. The geoLocationsID parameter is used to set a foreign key value
 * in the resulting GeoLocationEntity objects, likely for association with another entity in the
 * database.
 *
 * @param geoLocationsID the ID used for association purposes in the GeoLocationEntity objects.
 * @return a List of GeoLocationEntity objects with the specified association ID.
 */
fun List<GeoLocation>.toGeoLocationEntities(geoLocationsID: Long): List<GeoLocationEntity> {
    return map { it.toGeoLocationEntity(geoLocationsID = geoLocationsID) }
}

/**
 * Converts a List<GeoLocation> to a List<GeoLocationEntity> with a specified ID for association.
 *
 * This function use the ID of the [GeoLocation] object instead of create a new entity. Typically
 * for update purposes
 *
 * This function iterates through the provided List<GeoLocation> and converts each GeoLocation
 * object to a [GeoLocationEntity]. The geoLocationsID parameter is used to set a foreign key value
 * in the resulting GeoLocationEntity objects, likely for association with another entity in the
 * database.
 *
 * @param geoLocationsID the ID used for association purposes in the GeoLocationEntity objects.
 * @return a List of GeoLocationEntity objects with the specified association ID.
 */
fun List<GeoLocation>.toGeoLocationEntitiesWithID(geoLocationsID: Long): List<GeoLocationEntity> {
    return map { it.toGeoLocationEntity(id = it.id, geoLocationsID = geoLocationsID) }
}

/**
 * Converts a [CartographicBoundary] into the entity object.
 *
 * @return the [CartographicBoundary] converted into [CartographicBoundaryEntity]
 */
fun CartographicBoundary.toCartographicBoundaryEntity(): CartographicBoundaryEntity {
    return CartographicBoundaryEntity(
        administrativeUnitNameCartographicBoundariesID = administrativeUnitName.id,
        administrativeLevel = administrativeLevel.toString(),
        boundingBoxSouthwest = boundingBox.southwest.toGeoLocationEntity(),
        boundingBoxNortheast = boundingBox.northeast.toGeoLocationEntity(),
        zIndex = zIndex
    )
}

/**
 * Converts a [CartographicBoundary] into the entity object from a speficic ID.
 *
 * @param id the id used in the entity
 * @return the [CartographicBoundary] converted into [CartographicBoundaryEntity]
 */
fun CartographicBoundary.toCartographicBoundaryEntity(id: Long): CartographicBoundaryEntity {
    return CartographicBoundaryEntity(
        id = id,
        administrativeUnitNameCartographicBoundariesID = administrativeUnitName.id,
        administrativeLevel = administrativeLevel.toString(),
        boundingBoxSouthwest = boundingBox.southwest.toGeoLocationEntity(),
        boundingBoxNortheast = boundingBox.northeast.toGeoLocationEntity(),
        zIndex = zIndex
    )
}