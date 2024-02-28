package io.schiar.fridgnet.model.datasource

import io.schiar.fridgnet.model.Coordinate
import io.schiar.fridgnet.model.Image
import io.schiar.fridgnet.model.datasource.retriever.ImageRetriever

interface ImageDataSource: ImageRetriever {
    suspend fun setup(onLoaded: (image: Image) -> Unit)
    fun insert(image: Image)
    fun fetchImageBy(coordinate: Coordinate): Image?
    suspend fun deleteAll()
}