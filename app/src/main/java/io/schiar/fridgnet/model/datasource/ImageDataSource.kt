package io.schiar.fridgnet.model.datasource

import io.schiar.fridgnet.model.Coordinate
import io.schiar.fridgnet.model.Image
import io.schiar.fridgnet.model.datasource.retriever.ImageRetriever
import kotlinx.coroutines.flow.Flow

interface ImageDataSource: ImageRetriever {
    suspend fun create(image: Image)
    fun retrieve(): Flow<List<Image>>
    suspend fun retrieve(coordinate: Coordinate): Image?
    suspend fun delete()
}