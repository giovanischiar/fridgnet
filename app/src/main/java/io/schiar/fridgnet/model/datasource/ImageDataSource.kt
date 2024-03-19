package io.schiar.fridgnet.model.datasource

import io.schiar.fridgnet.model.Image
import io.schiar.fridgnet.model.ImageAdministrativeUnit
import kotlinx.coroutines.flow.Flow

interface ImageDataSource {
    suspend fun create(image: Image)
    suspend fun createFrom(uri: String)
    fun retrieve(): Flow<List<Image>>
    fun retrieveWithAdministrativeUnit(): Flow<List<ImageAdministrativeUnit>>
    suspend fun delete()
}