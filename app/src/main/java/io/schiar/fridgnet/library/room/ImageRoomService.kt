package io.schiar.fridgnet.library.room

import io.schiar.fridgnet.model.Image
import io.schiar.fridgnet.model.service.ImageService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ImageRoomService(private val imageDAO: ImageDAO) : ImageService {
    override fun retrieve(): Flow<List<Image>> {
        return imageDAO.selectImagesWithCoordinate().map { it.toImages() }
    }

    override suspend fun create(image: Image) {
        imageDAO.insert(image = image)
    }

    override suspend fun delete() {
        imageDAO.deleteAll()
    }
}