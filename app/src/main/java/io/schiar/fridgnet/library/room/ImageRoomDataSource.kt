package io.schiar.fridgnet.library.room

import io.schiar.fridgnet.model.AdministrativeUnitName
import io.schiar.fridgnet.model.Image
import io.schiar.fridgnet.model.datasource.ImageDataSource
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ImageRoomDataSource @Inject constructor(private val imageDAO: ImageDAO) : ImageDataSource {
    private val imagesSet = mutableSetOf<Pair<Image, AdministrativeUnitName?>>()

    override fun retrieve(): Flow<List<Image>> {
        return imageDAO.selectImagesWithAdministrativeUnitNameAndGeoLocation().map { it.toImages() }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun retrieveImageWithOptionalAdministrativeUnitName(): Flow<Pair<Image, AdministrativeUnitName?>> {
        return imageDAO.selectImagesWithGeoLocationAndAdministrativeUnitName()
            .flatMapLatest { imagesWithGeoLocationAndAdministrativeUnitNameList ->
                flow {
                    for (
                        imagesWithGeoLocationAndAdministrativeUnitName in
                        imagesWithGeoLocationAndAdministrativeUnitNameList
                    ) {
                        val imageAndAdministrativeUnitName
                            = imagesWithGeoLocationAndAdministrativeUnitName
                            .toImageAndAdministrativeUnitName()
                        if (imagesSet.add(element = imageAndAdministrativeUnitName)) {
                            emit(imageAndAdministrativeUnitName)
                        }
                    }
                }
            }
    }

    override suspend fun create(image: Image) {
        imageDAO.insert(image = image)
    }

    override suspend fun delete() {
        imageDAO.deleteAll()
    }
}