package io.schiar.fridgnet.library.room.relationentity

import androidx.room.Embedded
import androidx.room.Relation
import io.schiar.fridgnet.library.room.entity.AddressEntity
import io.schiar.fridgnet.library.room.entity.GeoLocationEntity
import io.schiar.fridgnet.library.room.entity.LocationEntity

data class AddressWithLocationsAndGeoLocations(
    @Embedded
    val addressEntity: AddressEntity,
    @Relation(
        entity = LocationEntity::class,
        parentColumn = "id",
        entityColumn = "addressLocationsID"
    )
    val locationEntities: List<LocationWithRegions>,
    @Relation(parentColumn = "id", entityColumn = "addressGeoLocationsID")
    val geoLocationEntities: List<GeoLocationEntity>
)
