package io.schiar.fridgnet.model.datasource

import io.schiar.fridgnet.model.AdministrativeUnitName
import io.schiar.fridgnet.model.Image
import kotlinx.coroutines.flow.Flow

/**
 * Interface representing a data source for managing [Image] objects. This data source could be
 * local storage, a remote server, or another image storage solution.
 */
interface ImageDataSource {
    /**
     * Creates a new [Image] in the data source.
     *
     * @param image the [Image] object to be created.
     */
    suspend fun create(image: Image)

    /**
     * Retrieves all available [Image] objects from the data source as a stream of lists using
     * Kotlin's [Flow] API.
     *
     * @return a [Flow] of lists containing all [Image] objects.
     */
    fun retrieve(): Flow<List<Image>>

    /**
     * Retrieves a stream (Flow) of pairs containing an [Image] object and a possibly missing
     * [AdministrativeUnitName] object. This flow emits whenever a new image is created or the
     * associated administrative unit name is updated.
     *
     * **Note:** The `AdministrativeUnitName` might be missing if the image is not associated with
     * any administrative unit or if the associated unit name is not yet available.
     *
     * @return a Flow of pairs: [Image] and [AdministrativeUnitName?]
     */
    fun retrieveImageWithOptionalAdministrativeUnitName()
        : Flow<Pair<Image, AdministrativeUnitName?>>

    /**
     * Deletes all images in the data source.
     */
    suspend fun delete()
}