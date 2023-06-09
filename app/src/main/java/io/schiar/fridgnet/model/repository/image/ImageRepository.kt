package io.schiar.fridgnet.model.repository.image

import io.schiar.fridgnet.model.BoundingBox
import io.schiar.fridgnet.model.Image

interface ImageRepository {
    suspend fun setup()
    suspend fun addImagesFromDatabase(onReady: suspend (image: Image) -> Unit)
    suspend fun addImages(uris: List<String>, onReady: suspend (image: Image) -> Unit)
    fun imagesThatIntersect(boundingBox: BoundingBox): List<Image>
    suspend fun removeAllImages()
}