package io.schiar.fridgnet.model.datasource

import io.schiar.fridgnet.model.Coordinate
import io.schiar.fridgnet.model.Image
import io.schiar.fridgnet.model.datasource.retriever.ImageRetriever

interface ImageDataSource: ImageRetriever {
    suspend fun setup(onLoaded: (image: Image) -> Unit)
    suspend fun create(image: Image)
    suspend fun retrieve(coordinate: Coordinate): Image?
    suspend fun delete()
}