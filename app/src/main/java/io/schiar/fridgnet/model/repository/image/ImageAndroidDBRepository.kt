package io.schiar.fridgnet.model.repository.image

import io.schiar.fridgnet.Log
import io.schiar.fridgnet.model.Address
import io.schiar.fridgnet.model.BoundingBox
import io.schiar.fridgnet.model.Coordinate
import io.schiar.fridgnet.model.Image
import io.schiar.fridgnet.model.datasource.ImageDataSource
import io.schiar.fridgnet.model.datasource.retriever.ImageRetriever
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.system.measureTimeMillis
import java.util.Collections.synchronizedMap as syncMapOf

class ImageAndroidDBRepository(
    private val imageRetriever: ImageRetriever,
    private val imageDataSource: ImageDataSource
) : ImageRepository {
    private val uriImage: MutableMap<String, Image> = syncMapOf(mutableMapOf())
    private val coordinateImage: MutableMap<Coordinate, Image> = syncMapOf(mutableMapOf())
    override var currentImages: Pair<Address, Set<Image>>? = null

    override suspend fun setup() {
        imageDataSource.setup(onLoaded = ::onLoaded)
    }

    override suspend fun addImagesFromDatabase(onReady: suspend (image: Image) -> Unit) {
        uriImage.values.forEach { image -> onReady(image) }
    }

    private fun onLoaded(image: Image) {
        uriImage[image.uri] = image
        coordinateImage[image.coordinate] = image
    }

    private suspend fun fetchImageBy(uri: String): Image? {
        log(uri = uri, "Let's check on the memory")
        return if (uriImage.containsKey(uri)) {
            log(uri = uri, "It's already on the memory! Returning...")
            uriImage[uri]
        } else {
            log(uri = uri, "Shoot! Time to search in the database")
            val imageFromDataSource = withContext(Dispatchers.IO) {
                imageDataSource.retrieve(uri = uri)
            }
            if (imageFromDataSource != null) {
                log(uri = uri, "it's on the database! Returning...")
                onLoaded(image = imageFromDataSource)
                imageFromDataSource
            } else {
                log(uri = uri, "Shoot! Time to search in the Android")
                val imageFromRetriever = withContext(Dispatchers.IO) {
                    imageRetriever.retrieve(uri = uri)
                }
                if (imageFromRetriever != null) {
                    log(uri = uri, "It's on the Android! Returning...")
                    onLoaded(image = imageFromRetriever)
                    coroutineScope {
                        launch(Dispatchers.IO) {
                            imageDataSource.create(image = imageFromRetriever)
                        }
                    }
                }
                imageFromRetriever
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

    override suspend fun imagesFromCoordinates(coordinates: Set<Coordinate>): Set<Image> {
        return coordinates.mapNotNull { coordinate ->
            fetchImageBy(coordinate)
        }.toSet()
    }

    private suspend fun fetchImageBy(coordinate: Coordinate): Image? {
        log(coordinate = coordinate, "Let's check on the memory")
        return if (coordinateImage.containsKey(coordinate)) {
            log(coordinate = coordinate, "It's already on the memory! Returning...")
            coordinateImage[coordinate]
        } else {
            log(coordinate = coordinate, "Shoot! Time to search in the database")
            val imageFromDatabase = withContext(Dispatchers.IO) {
                imageDataSource.retrieve(coordinate = coordinate)
            }
            log(coordinate = coordinate, "it's on the database! Returning...")
            if (imageFromDatabase != null) {
                onLoaded(image = imageFromDatabase)
            }
            imageFromDatabase
        }
    }

    override suspend fun removeAllImages() {
        uriImage.clear()
        coordinateImage.clear()
        imageDataSource.delete()
    }

    private fun log(coordinate: Coordinate, msg: String) {
        val (latitude, longitude) = coordinate
        Log.d(
            tag = "Uri to Image Feature",
            msg = "Fetching Image in ($latitude, $longitude): $msg"
        )
    }

    private fun log(uri: String, msg: String) {
        Log.d(tag = "Uri to Image Feature", msg = "Fetching Image of uri $uri: $msg")
    }
}