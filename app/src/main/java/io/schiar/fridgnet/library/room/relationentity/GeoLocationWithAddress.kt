package io.schiar.fridgnet.library.room.relationentity

import androidx.room.Embedded
import androidx.room.Relation
import io.schiar.fridgnet.library.room.entity.AddressEntity
import io.schiar.fridgnet.library.room.entity.GeoLocationEntity

data class GeoLocationWithAddress(
    @Embedded
    val geoLocationEntity: GeoLocationEntity,
    @Relation(parentColumn = "addressGeoLocationsID", entityColumn = "id")
    val addressEntity: AddressEntity?
)