package io.schiar.fridgnet.library.room

import io.schiar.fridgnet.library.room.entity.CoordinateEntity
import io.schiar.fridgnet.model.Coordinate

fun Coordinate.toCoordinateEntity(addressCoordinatesID: Long): CoordinateEntity {
    return CoordinateEntity(
        addressCoordinatesID = addressCoordinatesID,
        latitude = latitude,
        longitude = longitude
    )
}

fun Coordinate.toCoordinateEntity(coordinatesID: Long? = null): CoordinateEntity {
    return CoordinateEntity(
        coordinatesID = coordinatesID,
        latitude = latitude,
        longitude = longitude
    )
}