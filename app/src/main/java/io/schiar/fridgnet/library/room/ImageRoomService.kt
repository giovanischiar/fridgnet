package io.schiar.fridgnet.library.room

import io.schiar.fridgnet.model.AdministrativeUnit
import io.schiar.fridgnet.model.Image
import io.schiar.fridgnet.model.service.ImageService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ImageRoomService(private val imageDAO: ImageDAO) : ImageService {
    override fun retrieve(): Flow<List<Image>> {
        return imageDAO.selectImagesWithAdministrativeUnitAndGeoLocation().map { it.toImages() }
    }

    override fun retrieveWithAdministrativeUnit(): Flow<List<Pair<AdministrativeUnit?, Image>>> {
        return imageDAO.selectImagesWithGeoLocationAndAdministrativeUnit().map {
            it.toAdministrativeUnitAndImageList()
        }
    }

    override suspend fun create(image: Image) {
        imageDAO.insert(image = image)
    }

    override suspend fun delete() {
        imageDAO.deleteAll()
    }
}