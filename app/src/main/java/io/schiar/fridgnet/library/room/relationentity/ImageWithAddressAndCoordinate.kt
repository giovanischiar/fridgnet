package io.schiar.fridgnet.library.room.relationentity

import androidx.room.Embedded
import androidx.room.Relation
import io.schiar.fridgnet.library.room.entity.CoordinateEntity
import io.schiar.fridgnet.library.room.entity.ImageEntity

data class ImageWithAddressAndCoordinate(
    @Embedded
    val imageEntity: ImageEntity,
    @Relation(entity = CoordinateEntity::class, parentColumn = "coordinateID", entityColumn = "id")
    val coordinateWithAddress: CoordinateWithAddress,
)