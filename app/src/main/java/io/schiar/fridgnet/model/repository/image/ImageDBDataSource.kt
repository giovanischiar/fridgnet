package io.schiar.fridgnet.model.repository.image

import io.schiar.fridgnet.model.Coordinate
import io.schiar.fridgnet.model.Image
import io.schiar.fridgnet.model.datasource.room.ImageDAO
import io.schiar.fridgnet.model.repository.location.toCoordinateEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ImageDBDataSource(private val imageDAO: ImageDAO) : ImageDataSource {
    suspend fun setup(onLoaded: (image: Image) -> Unit) = coroutineScope {
        launch {
            withContext(Dispatchers.IO) { selectImages() }.forEach { image ->
                onLoaded(image)
            }
        }
    }

    private fun selectImages(): List<Image> {
        return imageDAO.selectImagesWithCoordinate().map { it.toImage() }
    }

    fun insert(image: Image) {
        val coordinateID = imageDAO.insert(coordinateEntity = image.coordinate.toCoordinateEntity())
        imageDAO.insert(imageEntity = image.toImageEntity(coordinateID = coordinateID))
    }

    override suspend fun fetchImageBy(uri: String): Image? {
        return imageDAO.selectImageBy(uri = uri)?.toImage()
    }

    fun fetchImageBy(coordinate: Coordinate): Image? {
        val (latitude, longitude) = coordinate
        return imageDAO.selectImageBy(latitude = latitude, longitude = longitude)?.toImage()
    }

    suspend fun deleteAll() = coroutineScope {
        launch(Dispatchers.IO) {
            imageDAO.deleteAll()
        }
    }
}