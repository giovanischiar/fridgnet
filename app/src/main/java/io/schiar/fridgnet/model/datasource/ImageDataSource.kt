package io.schiar.fridgnet.model.datasource

import io.schiar.fridgnet.model.Coordinate
import io.schiar.fridgnet.model.Image
import io.schiar.fridgnet.model.datasource.retriever.ImageRetriever

interface ImageDataSource: ImageRetriever {
    suspend fun setup(onLoaded: (image: Image) -> Unit)
    fun create(image: Image)
    fun retrieve(coordinate: Coordinate): Image?
    suspend fun delete()
}