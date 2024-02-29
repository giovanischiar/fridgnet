package io.schiar.fridgnet.model.datasource.retriever

import io.schiar.fridgnet.model.Image

interface ImageRetriever {
    suspend fun retrieve(uri: String): Image?
}