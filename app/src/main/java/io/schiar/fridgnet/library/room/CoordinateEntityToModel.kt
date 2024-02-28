package io.schiar.fridgnet.library.room

import io.schiar.fridgnet.library.room.entity.CoordinateEntity
import io.schiar.fridgnet.model.Coordinate

fun CoordinateEntity.toCoordinate(): Coordinate {
    return Coordinate(
        latitude = latitude,
        longitude = longitude
    )
}