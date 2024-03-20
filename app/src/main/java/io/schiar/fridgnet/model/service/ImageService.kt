package io.schiar.fridgnet.model.service

import io.schiar.fridgnet.model.AdministrativeUnit
import io.schiar.fridgnet.model.Image
import kotlinx.coroutines.flow.Flow

interface ImageService {
    suspend fun create(image: Image)
    fun retrieve(): Flow<List<Image>>
    fun retrieveWithAdministrativeUnit(): Flow<List<Pair<AdministrativeUnit?, Image>>>
    suspend fun delete()
}