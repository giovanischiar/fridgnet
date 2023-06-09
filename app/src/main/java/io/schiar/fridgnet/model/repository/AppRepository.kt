package io.schiar.fridgnet.model.repository

interface AppRepository {
    suspend fun loadDatabase(onDatabaseLoaded: () -> Unit)
    suspend fun addURIs(uris: List<String>)
}