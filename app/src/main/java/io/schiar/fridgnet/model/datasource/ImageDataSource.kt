package io.schiar.fridgnet.model.datasource

import io.schiar.fridgnet.model.AdministrativeUnitName
import io.schiar.fridgnet.model.Image
import kotlinx.coroutines.flow.Flow

interface ImageDataSource {
    suspend fun create(image: Image)
    suspend fun createFrom(uri: String)
    fun retrieve(): Flow<List<Image>>
    fun retrieveWithAdministrativeUnitName(): Flow<List<Pair<AdministrativeUnitName?, Image>>>
    suspend fun delete()
}