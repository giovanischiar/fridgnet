package io.schiar.fridgnet.library.room.relationentity

import androidx.room.Embedded
import androidx.room.Relation
import io.schiar.fridgnet.library.room.entity.AdministrativeUnitEntity
import io.schiar.fridgnet.library.room.entity.GeoLocationEntity

data class GeoLocationWithAdministrativeUnit(
    @Embedded
    val geoLocationEntity: GeoLocationEntity,
    @Relation(parentColumn = "administrativeUnitGeoLocationsID", entityColumn = "id")
    val administrativeUnitEntity: AdministrativeUnitEntity?
)