package io.schiar.fridgnet.model.repository.image

import io.schiar.fridgnet.Log
import io.schiar.fridgnet.model.BoundingBox
import io.schiar.fridgnet.model.Image
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*

class ImageURIRepository(private val dataSource: ImageDataSource): ImageRepository {
    private val images: MutableList<Image> = Collections.synchronizedList(mutableListOf())

    override suspend fun addImages(uris: List<String>, onReady: suspend (image: Image) -> Unit) {
        Log.d("Add Image Feature", "Adding images")
        withContext(Dispatchers.IO) {
            coroutineScope {
                launch(Dispatchers.IO) {
                    uris.forEach { uri ->
                        Log.d("Add Image Feature", "Adding $uri")
                        val coordinate = dataSource.extractCoordinate(uri = uri)
                        Log.d("Add Image Feature", "of Coordinate $coordinate")
                        val date = dataSource.extractDate(uri = uri)
                        Log.d("Add Image Feature", "and date $date")
                        val image = Image(
                            uri = uri,
                            coordinate = coordinate,
                            date = date
                        )
                        images.add(image)
                        Log.d("Add Image Feature", "Image $image added")
                        onReady(image)
                    }
                }
            }
        }
        Log.d("Add Image Feature", "All Images Added!")
    }

    override fun imagesThatIntersect(boundingBox: BoundingBox): List<Image> {
        return images.filter { image -> boundingBox.contains(coordinate = image.coordinate) }
    }
}