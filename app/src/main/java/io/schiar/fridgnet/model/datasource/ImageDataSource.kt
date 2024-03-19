package io.schiar.fridgnet.model.datasource

import io.schiar.fridgnet.model.Image
import io.schiar.fridgnet.model.ImageAddress
import kotlinx.coroutines.flow.Flow

interface ImageDataSource {
    suspend fun create(image: Image)
    suspend fun createFrom(uri: String)
    fun retrieve(): Flow<List<Image>>
    fun retrieveWithAddress(): Flow<List<ImageAddress>>
    suspend fun delete()
}