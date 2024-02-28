package io.schiar.fridgnet.model.repository.image

import io.schiar.fridgnet.model.Address
import io.schiar.fridgnet.model.BoundingBox
import io.schiar.fridgnet.model.Coordinate
import io.schiar.fridgnet.model.Image

interface ImageRepository {
    var currentImages: Pair<Address, Set<Image>>?
    suspend fun setup()
    suspend fun addImagesFromDatabase(onReady: suspend (image: Image) -> Unit)
    suspend fun addImages(uris: List<String>, onReady: suspend (image: Image) -> Unit)
    suspend fun imagesFromCoordinates(coordinates: Set<Coordinate>): Set<Image>
    fun imagesThatIntersect(boundingBox: BoundingBox): List<Image>
    suspend fun removeAllImages()
}