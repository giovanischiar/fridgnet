package io.schiar.fridgnet.model.repository

import io.schiar.fridgnet.model.datasource.ImageDataSource
import io.schiar.fridgnet.model.datasource.retriever.ImageRetriever
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

/**
 * The repository responsible for retrieving and creating images in the data source.
 * This repository manages the interaction with the data source to handle image retrieval
 * and creation operations.
 */
class HomeRepository @Inject constructor(
    private val imageRetriever: ImageRetriever,
    private val imageDataSource: ImageDataSource
) {
    /**
     * Get the uri list sent from view, transform in model object image, and store it in the
     * database.
     */
    suspend fun addURIs(uris: List<String>) {
        imageRetriever.retrieve(uris = uris.shuffled()).onEach(imageDataSource::create).collect()
    }
}