package io.schiar.fridgnet.model.repository.image

import io.schiar.fridgnet.model.Image

interface ImageDataSource {
    suspend fun fetchImageBy(uri: String): Image?
}