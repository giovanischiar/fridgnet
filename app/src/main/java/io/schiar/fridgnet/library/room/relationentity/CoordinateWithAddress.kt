package io.schiar.fridgnet.library.room.relationentity

import androidx.room.Embedded
import androidx.room.Relation
import io.schiar.fridgnet.library.room.entity.AddressEntity
import io.schiar.fridgnet.library.room.entity.CoordinateEntity

data class CoordinateWithAddress(
    @Embedded
    val coordinateEntity: CoordinateEntity,
    @Relation(parentColumn = "addressCoordinatesID", entityColumn = "id")
    val addressEntity: AddressEntity?
)