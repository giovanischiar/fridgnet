package io.schiar.fridgnet.model.repository.image

import io.schiar.fridgnet.model.BoundingBox
import io.schiar.fridgnet.model.Image

interface ImageRepository {
    suspend fun addImages(uris: List<String>, onReady: suspend (image: Image) -> Unit)
    fun imagesThatIntersect(boundingBox: BoundingBox): List<Image>
}