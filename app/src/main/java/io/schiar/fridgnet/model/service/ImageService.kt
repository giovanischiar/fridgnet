package io.schiar.fridgnet.model.service

import io.schiar.fridgnet.model.AdministrativeUnitName
import io.schiar.fridgnet.model.Image
import kotlinx.coroutines.flow.Flow

interface ImageService {
    suspend fun create(image: Image)
    fun retrieve(): Flow<List<Image>>
    fun retrieveWithAdministrativeUnitName(): Flow<List<Pair<AdministrativeUnitName?, Image>>>
    suspend fun delete()
}