package io.schiar.fridgnet.library.room

import io.schiar.fridgnet.library.room.entity.ImageEntity
import io.schiar.fridgnet.library.room.relationentity.ImageWithCoordinate
import io.schiar.fridgnet.library.room.toCoordinate
import io.schiar.fridgnet.model.Image

fun Image.toImageEntity(coordinateID: Long): ImageEntity {
    return ImageEntity(
        uri = uri,
        byteArray = byteArray,
        date = date,
        coordinateID = coordinateID
    )
}