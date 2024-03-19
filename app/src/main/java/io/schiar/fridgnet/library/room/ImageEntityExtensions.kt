package io.schiar.fridgnet.library.room

import io.schiar.fridgnet.library.room.entity.ImageEntity
import io.schiar.fridgnet.model.Image

fun Image.toImageEntity(geoLocationID: Long): ImageEntity {
    return ImageEntity(
        uri = uri,
        byteArray = byteArray,
        date = date,
        geoLocationID = geoLocationID
    )
}