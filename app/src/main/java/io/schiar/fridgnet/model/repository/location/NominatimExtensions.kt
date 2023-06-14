package io.schiar.fridgnet.model.repository.location

import io.schiar.fridgnet.model.BoundingBox
import io.schiar.fridgnet.model.Coordinate

fun List<Double>.toCoordinate(): Coordinate {
    return Coordinate(latitude = this[1], longitude = this[0])
}

fun List<List<Double>>.toLineStringCoordinates(): List<Coordinate> {
    return map { it.toCoordinate() }
}

fun List<List<List<Double>>>.toPolygonCoordinates(): List<List<Coordinate>> {
    return map { it.toLineStringCoordinates() }
}

fun List<List<List<List<Double>>>>.toMultiPolygonCoordinates(): List<List<List<Coordinate>>> {
    return map { it.toPolygonCoordinates() }
}

fun List<String>.toBoundingBox(): BoundingBox {
    val southwest = Coordinate(latitude = this[0].toDouble(), longitude = this[2].toDouble())
    val northeast = Coordinate(latitude = this[1].toDouble(), longitude = this[3].toDouble())
    return BoundingBox(southwest = southwest, northeast = northeast)
}