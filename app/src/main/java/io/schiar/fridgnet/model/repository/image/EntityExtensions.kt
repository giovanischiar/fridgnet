package io.schiar.fridgnet.model.repository.image

import io.schiar.fridgnet.model.Image
import io.schiar.fridgnet.model.datasource.room.entity.ImageEntity
import io.schiar.fridgnet.model.datasource.room.relationentity.ImageWithCoordinate
import io.schiar.fridgnet.model.repository.location.toCoordinate

fun ImageWithCoordinate.toImage(): Image {
    return Image(
        uri = imageEntity.uri,
        byteArray = imageEntity.byteArray,
        date = imageEntity.date,
        coordinate = coordinate.toCoordinate()
    )
}

fun Image.toImageEntity(coordinateID: Long): ImageEntity {
    return ImageEntity(
        uri = uri,
        byteArray = byteArray,
        date = date,
        coordinateID = coordinateID
    )
}