package io.schiar.fridgnet.model.datasource

import io.schiar.fridgnet.model.CartographicBoundary
import io.schiar.fridgnet.model.Region
import kotlinx.coroutines.flow.Flow

/**
 * Interface representing a data source for managing [CartographicBoundary] objects. This data source
 * could be local storage, a remote server, or another solution for storing cartographic boundaries.
 */
interface CartographicBoundaryDataSource {
    /**
     * Creates a new [CartographicBoundary] in the data source.
     *
     * @param cartographicBoundary the [CartographicBoundary] object to be created.
     */
    suspend fun create(cartographicBoundary: CartographicBoundary)

    /**
     * Retrieves a stream ([Flow]) of all [CartographicBoundary] objects from the data source. This
     * Flow will emit updates whenever a new cartographic boundary is created or an existing one is
     * updated.
     *
     * @return a Flow of [CartographicBoundary] objects.
     */
    fun retrieve(): Flow<CartographicBoundary>

    /**
     * Retrieves a stream (Flow) of [CartographicBoundary] objects associated with a specific
     * [Region]. The Flow might emit null if no cartographic boundary is found for the provided
     * region.
     *
     * @param region the [Region] used to filter the boundaries.
     * @return       a Flow of [CartographicBoundary] objects (nullable).
     */
    fun retrieve(region: Region): Flow<CartographicBoundary?>

    /**
     * Updates an existing [CartographicBoundary] in the data source.
     *
     * @param cartographicBoundary the [CartographicBoundary] object with the updated information.
     */
    suspend fun update(cartographicBoundary: CartographicBoundary)
}