package io.schiar.fridgnet.library.room.relationentity

import androidx.room.Embedded
import androidx.room.Relation
import io.schiar.fridgnet.library.room.entity.AdministrativeUnitNameEntity
import io.schiar.fridgnet.library.room.entity.GeoLocationEntity

/**
 * This class represents the combined result of retrieving a GeoLocation entity along with its
 * optional associated AdministrativeUnitName entity.

 * @property geoLocationEntity The geolocation data retrieved from the database.
 * @property administrativeUnitNameEntity The administrative unit name associated with the
 * geolocation (optional one-to-one relationship).
 */
data class GeoLocationWithAdministrativeUnitName(
    @Embedded
    val geoLocationEntity: GeoLocationEntity,
    @Relation(parentColumn = "administrativeUnitNameGeoLocationsID", entityColumn = "id")
    val administrativeUnitNameEntity: AdministrativeUnitNameEntity?
)