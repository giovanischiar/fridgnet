package io.schiar.fridgnet.model.repository

import io.schiar.fridgnet.Log
import io.schiar.fridgnet.model.datasource.ImageDataSource
import io.schiar.fridgnet.model.datasource.retriever.ImageRetriever
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

class AppRepository @Inject constructor(
    private val imageRetriever: ImageRetriever,
    private val imageDataSource: ImageDataSource
) {
    suspend fun addURIs(uris: List<String>) {
        imageRetriever.retrieve(uris = uris.shuffled()).onEach(imageDataSource::create).collect()
    }

    fun log(msg: String) {
        val methodName = Thread.currentThread().stackTrace[3].methodName
        Log.d(tag = "AppRepository.$methodName", msg = msg)
    }
}