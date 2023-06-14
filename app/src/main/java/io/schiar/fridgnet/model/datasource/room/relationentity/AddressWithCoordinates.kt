package io.schiar.fridgnet.model.datasource.room.relationentity

import androidx.room.Embedded
import androidx.room.Relation
import io.schiar.fridgnet.model.datasource.room.entity.AddressEntity
import io.schiar.fridgnet.model.datasource.room.entity.CoordinateEntity

data class AddressWithCoordinates(
    @Embedded
    val addressEntity: AddressEntity,
    @Relation(parentColumn = "id", entityColumn = "addressCoordinatesID")
    val coordinates: List<CoordinateEntity>
)
