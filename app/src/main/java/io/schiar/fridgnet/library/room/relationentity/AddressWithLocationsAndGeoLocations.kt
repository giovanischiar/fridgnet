package io.schiar.fridgnet.library.room.relationentity

import androidx.room.Embedded
import androidx.room.Relation
import io.schiar.fridgnet.library.room.entity.AdministrativeUnitEntity
import io.schiar.fridgnet.library.room.entity.GeoLocationEntity
import io.schiar.fridgnet.library.room.entity.LocationEntity

data class AdministrativeUnitWithLocationsAndGeoLocations(
    @Embedded
    val administrativeUnitEntity: AdministrativeUnitEntity,
    @Relation(
        entity = LocationEntity::class,
        parentColumn = "id",
        entityColumn = "administrativeUnitLocationsID"
    )
    val locationEntities: List<LocationWithRegions>,
    @Relation(parentColumn = "id", entityColumn = "administrativeUnitGeoLocationsID")
    val geoLocationEntities: List<GeoLocationEntity>
)
