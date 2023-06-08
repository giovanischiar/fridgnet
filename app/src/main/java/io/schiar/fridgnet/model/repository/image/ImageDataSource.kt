package io.schiar.fridgnet.model.repository.image

import io.schiar.fridgnet.model.Coordinate

interface ImageDataSource {
    fun extractCoordinate(uri: String): Coordinate
    fun extractDate(uri: String): Long
}