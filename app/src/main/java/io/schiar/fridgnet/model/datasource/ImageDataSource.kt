package io.schiar.fridgnet.model.datasource

import io.schiar.fridgnet.model.AdministrativeUnitName
import io.schiar.fridgnet.model.Image
import kotlinx.coroutines.flow.Flow

interface ImageDataSource {
    suspend fun create(image: Image)
    fun retrieve(): Flow<List<Image>>
    fun retrieveWithAdministrativeUnitName(): Flow<Pair<Image, AdministrativeUnitName?>>
    suspend fun delete()
}