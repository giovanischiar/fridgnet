package io.schiar.fridgnet.library.room.relationentity

import androidx.room.Embedded
import androidx.room.Relation
import io.schiar.fridgnet.library.room.entity.AddressEntity
import io.schiar.fridgnet.library.room.entity.CoordinateEntity
import io.schiar.fridgnet.library.room.entity.LocationEntity

data class AddressWithLocationsAndCoordinates(
    @Embedded
    val addressEntity: AddressEntity,
    @Relation(
        entity = LocationEntity::class,
        parentColumn = "id",
        entityColumn = "addressLocationsID"
    )
    val locations: List<LocationWithRegions>,
    @Relation(parentColumn = "id", entityColumn = "addressCoordinatesID")
    val coordinates: List<CoordinateEntity>
)
