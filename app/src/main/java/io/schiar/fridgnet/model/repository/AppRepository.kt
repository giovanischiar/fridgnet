package io.schiar.fridgnet.model.repository

import io.schiar.fridgnet.Log
import io.schiar.fridgnet.model.datasource.ImageDataSource
import io.schiar.fridgnet.model.datasource.retriever.ImageRetriever

class AppRepository(
    private val imageRetriever: ImageRetriever,
    private val imageDataSource: ImageDataSource
) {
    suspend fun addURIs(uris: List<String>) {
        uris.shuffled().forEach { uri ->
            val imageFromRetriever = imageRetriever.retrieve(uri = uri)
            if (imageFromRetriever != null) {
                imageDataSource.create(image = imageFromRetriever)
                return@forEach
            }
            log(msg = "Image of uri $uri id not on the Retriever!")
        }
    }

    fun log(msg: String) {
        val methodName = Thread.currentThread().stackTrace[3].methodName
        Log.d(tag = "AppRepository.$methodName", msg = msg)
    }
}