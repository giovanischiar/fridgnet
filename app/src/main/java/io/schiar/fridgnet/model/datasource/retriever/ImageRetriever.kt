package io.schiar.fridgnet.model.datasource.retriever

import io.schiar.fridgnet.model.Image

interface ImageRetriever {
    suspend fun fetchImageBy(uri: String): Image?
}