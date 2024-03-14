package io.schiar.fridgnet.model.repository

import io.schiar.fridgnet.model.datasource.ImageDataSource

class AppRepository(private val imageDataSource: ImageDataSource) {
    suspend fun addURIs(uris: List<String>) {
        uris.forEach { uri -> imageDataSource.createFrom(uri = uri) }
    }
}