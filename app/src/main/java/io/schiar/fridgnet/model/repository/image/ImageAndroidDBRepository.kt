package io.schiar.fridgnet.model.repository.image

import io.schiar.fridgnet.Log
import io.schiar.fridgnet.model.BoundingBox
import io.schiar.fridgnet.model.Image
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.system.measureTimeMillis
import java.util.Collections.synchronizedMap as syncMapOf

class ImageAndroidDBRepository(
    private val imageAndroidDataSource: ImageAndroidDataSource,
    private val imageDBDataSource: ImageDBDataSource
): ImageRepository {
    private val uriImage: MutableMap<String, Image> = syncMapOf(mutableMapOf())

    override suspend fun setup() {
        imageDBDataSource.setup(onLoaded = ::onLoaded)
    }

    override suspend fun addImagesFromDatabase(onReady: suspend (image: Image) -> Unit) {
        uriImage.values.forEach { image -> onReady(image) }
    }

    private fun onLoaded(image: Image) {
        uriImage[image.uri] = image
    }

    private suspend fun fetchImageBy(uri: String): Image? {
        log(uri = uri, "Let's check on the memory")
        return if (uriImage.containsKey(uri)) {
            log(uri = uri, "It's already on the memory! Returning...")
            uriImage[uri]
        } else {
            log(uri = uri, "Shoot! Time to search in the database")
            val imageFromDatabase = withContext(Dispatchers.IO) {
                imageDBDataSource.fetchImageBy(uri = uri)
            }
            if (imageFromDatabase != null) {
                log(uri = uri, "it's on the database! Returning...")
                onLoaded(image = imageFromDatabase)
                imageFromDatabase
            } else {
                log(uri = uri, "Shoot! Time to search in the Android")
                val imageFromAndroid = withContext(Dispatchers.IO) {
                    imageAndroidDataSource.fetchImageBy(uri = uri)
                }
                if (imageFromAndroid != null) {
                    log(uri = uri, "It's on the Android! Returning...")
                    onLoaded(image = imageFromAndroid)
                    coroutineScope {
                        launch(Dispatchers.IO) {
                            imageDBDataSource.insert(image = imageFromAndroid)
                        }
                    }
                }
                imageFromAndroid
            }
        }
    }

    override suspend fun addImages(uris: List<String>, onReady: suspend (image: Image) -> Unit) {
        Log.d("Add Image Feature", "Adding images")
        val elapsed = measureTimeMillis {
            withContext(Dispatchers.IO) {
                coroutineScope {
                    launch(Dispatchers.IO) {
                        for (uri in uris) {
                            val image = fetchImageBy(uri = uri) ?: continue
                            Log.d("Add Image Feature", "Adding $image")
                            onReady(image)
                        }
                    }
                }
            }
        }
        Log.d("Add Image Feature", "All Images Added! time elapsed: $elapsed")
    }

    override fun imagesThatIntersect(boundingBox: BoundingBox): List<Image> {
        return uriImage.values.filter { image ->
            boundingBox.contains(coordinate = image.coordinate)
        }
    }

    override suspend fun removeAllImages() {
        uriImage.clear()
        imageDBDataSource.deleteAll()
    }

    private fun log(uri: String, msg: String) {
        Log.d(tag = "Uri to Image Feature", msg = "Fetching Image of uri $uri: $msg")
    }
}