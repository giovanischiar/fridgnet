package io.schiar.fridgnet.model.datasource.local

import io.schiar.fridgnet.Log
import io.schiar.fridgnet.model.Image
import io.schiar.fridgnet.model.datasource.ImageDataSource
import io.schiar.fridgnet.model.datasource.retriever.ImageRetriever
import io.schiar.fridgnet.model.service.ImageService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import java.util.Collections.synchronizedMap as syncMapOf
import java.util.Collections.synchronizedSet as syncSetOf

class ImageAndroidDBDataSource(
    private val imageRetriever: ImageRetriever,
    private val imageService: ImageService
): ImageDataSource {
    private val uriSet: MutableSet<String> = syncSetOf(mutableSetOf())
    private val uriImage: MutableMap<String, Image> = syncMapOf(mutableMapOf())
    private val imagesCacheFlow = MutableStateFlow(uriImage.values.toList())

    override suspend fun create(image: Image) {
        updateCache(uri = image.uri, image = image)
        imagesCacheFlow.update { uriImage.values.toList() }
        imageService.create(image = image)
    }

    private fun updateCache(uri: String, image: Image) { uriImage[uri] = image }

    private fun updateCache(images: List<Image>) { images.forEach { image ->
        updateCache(uri = image.uri, image = image) }
    }

    override suspend fun createFrom(uri: String) {
        if (uriSet.contains(element = uri)) return
        uriSet.add(element = uri)
        log(uri = uri, "It's not on memory, retrieving using the Android API")
        val imageFromRetriever = imageRetriever.retrieve(uri = uri)
        if (imageFromRetriever != null) {
            create(image = imageFromRetriever)
            return
        }
        log(uri = uri, "It's not on the Android!")
    }

    override fun retrieve(): Flow<List<Image>> {
        return merge(
            imagesCacheFlow,
            imageService.retrieve().onEach(::updateCache)
        ).distinctUntilChanged()
    }

    override suspend fun delete() {
        uriImage.clear()
        imageService.delete()
    }

    private fun log(uri: String, msg: String) {
        Log.d(tag = "Uri to Image Feature", msg = "Fetching Image of uri $uri: $msg")
    }
}