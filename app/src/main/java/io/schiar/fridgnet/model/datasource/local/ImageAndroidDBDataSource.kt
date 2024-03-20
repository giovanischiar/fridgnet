package io.schiar.fridgnet.model.datasource.local

import io.schiar.fridgnet.Log
import io.schiar.fridgnet.model.AdministrativeUnitName
import io.schiar.fridgnet.model.Image
import io.schiar.fridgnet.model.datasource.ImageDataSource
import io.schiar.fridgnet.model.datasource.retriever.ImageRetriever
import io.schiar.fridgnet.model.service.ImageService
import kotlinx.coroutines.flow.Flow

class ImageAndroidDBDataSource(
    private val imageRetriever: ImageRetriever,
    private val imageService: ImageService
): ImageDataSource {
    override suspend fun create(image: Image) { imageService.create(image = image) }

    override suspend fun createFrom(uri: String) {
        log(uri = uri, "It's not on memory, retrieving using the Android API")
        val imageFromRetriever = imageRetriever.retrieve(uri = uri)
        if (imageFromRetriever != null) {
            create(image = imageFromRetriever)
            return
        }
        log(uri = uri, "It's not on the Android!")
    }

    override fun retrieveWithAdministrativeUnitName(): Flow<List<Pair<AdministrativeUnitName?, Image>>> {
        return imageService.retrieveWithAdministrativeUnitName()
    }

    override fun retrieve(): Flow<List<Image>> { return imageService.retrieve() }

    override suspend fun delete() { imageService.delete() }

    private fun log(uri: String, msg: String) {
        Log.d(tag = "Uri to Image Feature", msg = "Fetching Image of uri $uri: $msg")
    }
}