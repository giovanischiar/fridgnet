package io.schiar.fridgnet.library.room.relationentity

import androidx.room.Embedded
import androidx.room.Relation
import io.schiar.fridgnet.library.room.entity.GeoLocationEntity
import io.schiar.fridgnet.library.room.entity.ImageEntity

/**
 * This class represents the combined result of retrieving an Image entity along with its associated geolocation data
 * and optional administrative unit name.
 *
 * @property imageEntity The image data retrieved from the database.
 * @property geoLocationWithAdministrativeUnitName The geolocation data and its optional associated
 * administrative unit name (one-to-one relationship).
 */
data class ImageWithAdministrativeUnitNameAndGeoLocation(
    @Embedded
    val imageEntity: ImageEntity,
    @Relation(
        entity = GeoLocationEntity::class, parentColumn = "geoLocationID", entityColumn = "id"
    )
    val geoLocationWithAdministrativeUnitName: GeoLocationWithAdministrativeUnitName,
)