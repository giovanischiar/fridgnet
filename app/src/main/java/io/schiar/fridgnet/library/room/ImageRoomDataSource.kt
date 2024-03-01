package io.schiar.fridgnet.library.room

import io.schiar.fridgnet.model.Coordinate
import io.schiar.fridgnet.model.Image
import io.schiar.fridgnet.model.datasource.ImageDataSource

class ImageRoomDataSource(private val imageDAO: ImageDAO) : ImageDataSource {
    override suspend fun setup(onLoaded: (image: Image) -> Unit) {
        selectImages().forEach { image -> onLoaded(image) }
    }

    private suspend fun selectImages(): List<Image> {
        return imageDAO.selectImagesWithCoordinate().map { it.toImage() }
    }

    override suspend fun create(image: Image) {
        val coordinateID = imageDAO.insert(coordinateEntity = image.coordinate.toCoordinateEntity())
        imageDAO.insert(imageEntity = image.toImageEntity(coordinateID = coordinateID))
    }

    override suspend fun retrieve(uri: String): Image? {
        return imageDAO.selectImageBy(uri = uri)?.toImage()
    }

    override suspend fun retrieve(coordinate: Coordinate): Image? {
        val (latitude, longitude) = coordinate
        return imageDAO.selectImageBy(latitude = latitude, longitude = longitude)?.toImage()
    }

    override suspend fun delete() {
        imageDAO.deleteAll()
    }
}