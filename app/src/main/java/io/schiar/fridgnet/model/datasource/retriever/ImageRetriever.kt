package io.schiar.fridgnet.model.datasource.retriever

import io.schiar.fridgnet.model.Image
import kotlinx.coroutines.flow.Flow

interface ImageRetriever {
    suspend fun retrieve(uris: List<String>): Flow<Image>
}