package io.schiar.fridgnet.library.room

import io.schiar.fridgnet.model.Coordinate
import io.schiar.fridgnet.model.Image
import io.schiar.fridgnet.model.datasource.ImageDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ImageRoomDataSource(private val imageDAO: ImageDAO) : ImageDataSource {
    override suspend fun setup(onLoaded: (image: Image) -> Unit): Unit = coroutineScope {
        launch {
            withContext(Dispatchers.IO) { selectImages() }.forEach { image ->
                onLoaded(image)
            }
        }
    }

    private fun selectImages(): List<Image> {
        return imageDAO.selectImagesWithCoordinate().map { it.toImage() }
    }

    override fun create(image: Image) {
        val coordinateID = imageDAO.insert(coordinateEntity = image.coordinate.toCoordinateEntity())
        imageDAO.insert(imageEntity = image.toImageEntity(coordinateID = coordinateID))
    }

    override suspend fun retrieve(uri: String): Image? {
        return imageDAO.selectImageBy(uri = uri)?.toImage()
    }

    override fun retrieve(coordinate: Coordinate): Image? {
        val (latitude, longitude) = coordinate
        return imageDAO.selectImageBy(latitude = latitude, longitude = longitude)?.toImage()
    }

    override suspend fun delete(): Unit = coroutineScope {
        launch(Dispatchers.IO) {
            imageDAO.deleteAll()
        }
    }
}