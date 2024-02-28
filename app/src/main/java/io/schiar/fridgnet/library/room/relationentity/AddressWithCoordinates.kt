package io.schiar.fridgnet.library.room.relationentity

import androidx.room.Embedded
import androidx.room.Relation
import io.schiar.fridgnet.library.room.entity.AddressEntity
import io.schiar.fridgnet.library.room.entity.CoordinateEntity

data class AddressWithCoordinates(
    @Embedded
    val addressEntity: AddressEntity,
    @Relation(parentColumn = "id", entityColumn = "addressCoordinatesID")
    val coordinates: List<CoordinateEntity>
)
