package io.schiar.fridgnet.model.service

import io.schiar.fridgnet.model.Image
import io.schiar.fridgnet.model.ImageAddress
import kotlinx.coroutines.flow.Flow

interface ImageService {
    suspend fun create(image: Image)
    fun retrieve(): Flow<List<Image>>
    fun retrieveWithAddress(): Flow<List<ImageAddress>>
    suspend fun delete()
}