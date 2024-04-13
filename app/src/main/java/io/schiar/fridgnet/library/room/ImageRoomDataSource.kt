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

/**
 * Implementation of [ImageDataSource] that utilizes Room Database for CRUD
 * (Create, Read, Update, Delete) operations on [Image] data. This class also
 * handles the association with [AdministrativeUnitName]s
 *
 * It leverages dependency injection to receive an instance of [ImageDAO]
 * for interacting with the database layer.
 */
class ImageRoomDataSource @Inject constructor(private val imageDAO: ImageDAO) : ImageDataSource {
    private val imagesSet = mutableSetOf<Pair<Image, AdministrativeUnitName?>>()

    /**
     * Retrieves a Flow of Lists containing all Image objects from the database.
     *
     * This method delegates the retrieval to the imageDAO using its
     * `selectImagesWithGeoLocationAndAdministrativeUnitName` method. The retrieved data
     * might contain additional information about GeoLocation and AdministrativeUnitName
     * (depending on the DAO implementation).
     *
     * This method then extracts and returns a Flow of Lists containing just the
     * Image objects (potentially after filtering or transforming the retrieved data using
     * toImages()).
     *
     * @return a Flow that emits Lists of Image objects.
     */
    override fun retrieve(): Flow<List<Image>> {
        return imageDAO.selectImagesWithGeoLocationAndAdministrativeUnitName().map { it.toImages() }
    }

    /**
     * Retrieves a Flow of [Pair]<[Image], [AdministrativeUnitName]?>.
     *
     * This method retrieves [Image] data along with an possibly missing associated
     * [AdministrativeUnitName] entity. It utilizes [Flow] to emit data asynchronously.
     *
     * To avoid emitting duplicate [Image] entries, it maintains a set of
     * IDs (imagesSet). Only new (unseen) entries are emitted.
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    override fun retrieveImageWithOptionalAdministrativeUnitName()
        : Flow<Pair<Image, AdministrativeUnitName?>> {
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

    /**
     * Inserts a [Image] object into the database.
     *
     * This method delegates the insert operation to the `imageDAO.insert` method.
     * Please refer to the documentation for `imageDAO.insert` for details on
     * transaction handling, potential error conditions, and any basic data validation performed
     * on the Image object before insertion.
     *
     * @param image the [Image] object to insert.
     */
    override suspend fun create(image: Image) {
        imageDAO.insert(image = image)
    }

    /**
     * Deletes images from database
     */
    override suspend fun delete() {
        imageDAO.deleteAll()
    }
}