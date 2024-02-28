package io.schiar.fridgnet.library.room.relationentity

import androidx.room.Embedded
import androidx.room.Relation
import io.schiar.fridgnet.library.room.entity.CoordinateEntity
import io.schiar.fridgnet.library.room.entity.ImageEntity

data class ImageWithCoordinate(
    @Embedded
    val imageEntity: ImageEntity,
    @Relation(parentColumn = "coordinateID", entityColumn = "id")
    val coordinate: CoordinateEntity
)