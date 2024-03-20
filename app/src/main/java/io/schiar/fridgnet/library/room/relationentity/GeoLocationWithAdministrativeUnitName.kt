package io.schiar.fridgnet.library.room.relationentity

import androidx.room.Embedded
import androidx.room.Relation
import io.schiar.fridgnet.library.room.entity.AdministrativeUnitNameEntity
import io.schiar.fridgnet.library.room.entity.GeoLocationEntity

data class GeoLocationWithAdministrativeUnitName(
    @Embedded
    val geoLocationEntity: GeoLocationEntity,
    @Relation(parentColumn = "administrativeUnitNameGeoLocationsID", entityColumn = "id")
    val administrativeUnitNameEntity: AdministrativeUnitNameEntity?
)