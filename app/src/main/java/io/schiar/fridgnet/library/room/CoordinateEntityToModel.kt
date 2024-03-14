package io.schiar.fridgnet.library.room

import io.schiar.fridgnet.library.room.entity.CoordinateEntity
import io.schiar.fridgnet.model.Coordinate

fun List<CoordinateEntity>.toCoordinates(): List<Coordinate> {
    return map { it.toCoordinate() }
}

fun CoordinateEntity.toCoordinate(): Coordinate {
    return Coordinate(
        id = id,
        latitude = latitude,
        longitude = longitude
    )
}