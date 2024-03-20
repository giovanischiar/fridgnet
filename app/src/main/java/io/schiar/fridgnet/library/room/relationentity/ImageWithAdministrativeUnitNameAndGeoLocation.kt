package io.schiar.fridgnet.library.room.relationentity

import androidx.room.Embedded
import androidx.room.Relation
import io.schiar.fridgnet.library.room.entity.GeoLocationEntity
import io.schiar.fridgnet.library.room.entity.ImageEntity

data class ImageWithAdministrativeUnitNameAndGeoLocation(
    @Embedded
    val imageEntity: ImageEntity,
    @Relation(
        entity = GeoLocationEntity::class, parentColumn = "geoLocationID", entityColumn = "id"
    )
    val geoLocationWithAdministrativeUnitName: GeoLocationWithAdministrativeUnitName,
)