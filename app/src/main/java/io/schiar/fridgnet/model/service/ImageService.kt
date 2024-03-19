package io.schiar.fridgnet.model.service

import io.schiar.fridgnet.model.Image
import io.schiar.fridgnet.model.ImageAdministrativeUnit
import kotlinx.coroutines.flow.Flow

interface ImageService {
    suspend fun create(image: Image)
    fun retrieve(): Flow<List<Image>>
    fun retrieveWithAdministrativeUnit(): Flow<List<ImageAdministrativeUnit>>
    suspend fun delete()
}