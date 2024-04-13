package io.schiar.fridgnet.library.room

import io.schiar.fridgnet.model.AdministrativeUnitName
import io.schiar.fridgnet.model.CartographicBoundary
import io.schiar.fridgnet.model.Region
import io.schiar.fridgnet.model.datasource.CartographicBoundaryDataSource
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * Implementation of [CartographicBoundaryDataSource] that utilizes Room Database for CRUD
 * (Create, Read, Update, Delete) operations on [CartographicBoundary] data. This class also
 * handles the association with [Region]s
 *
 * It leverages dependency injection to receive an instance of [CartographicBoundaryDAO]
 * for interacting with the database layer.
 */
class CartographicBoundaryRoomDataSource @Inject constructor(
    private val cartographicBoundaryDAO: CartographicBoundaryDAO
) : CartographicBoundaryDataSource {
    private val cartographicBoundariesSet = mutableSetOf<CartographicBoundary>()

    /**
     * Retrieves a Flow of [List]<[CartographicBoundary]>.
     *
     * This method retrieves [CartographicBoundary] data along with any existing associated
     * [Region] entities. It utilizes [Flow] to emit data asynchronously.
     *
     * To avoid emitting duplicate [CartographicBoundary] entries, it maintains a set of
     * IDs (cartographicBoundariesSet). Only new (unseen) entries are emitted.
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    override fun retrieve(): Flow<CartographicBoundary> {
        return cartographicBoundaryDAO.selectCartographicBoundariesWithRegions()
            .flatMapLatest { cartographicBoundaryWithRegionsList ->
                flow {
                    for (cartographicBoundaryWithRegions in cartographicBoundaryWithRegionsList) {
                        val cartographicBoundary = cartographicBoundaryWithRegions
                            .toCartographicBoundary()
                        if (cartographicBoundariesSet.add(element = cartographicBoundary)) {
                            emit(cartographicBoundary)
                        }
                    }
                }
            }
    }

    /**
     * Retrieves a Flow of [CartographicBoundary] objects that contain a specific [Region].
     *
     * This method utilizes a Flow to potentially emit updates in the future if a
     * CartographicBoundary containing the provided region is inserted, updated, or deleted
     * in the database. The initial emission will be null if no matching CartographicBoundary
     * is found for the provided region.
     *
     * @param region the region used to search for containing CartographicBoundaries.
     * @return a Flow of CartographicBoundary objects (can be null initially) that might emit
     * updates in the future based on changes in the database.
     */
    override fun retrieve(region: Region): Flow<CartographicBoundary?> {
        return cartographicBoundaryDAO.select(regionID = region.id)
            .map { cartographicBoundaryWithRegions ->
                cartographicBoundaryWithRegions?.toCartographicBoundary()
            }
    }

    /**
     * Retrieves a [CartographicBoundary] given a [AdministrativeUnitName] The initial emission will
     * be null if no matching CartographicBoundary is found for the provided administrative unit
     * name.
     *
     * @param administrativeUnitName the administrativeUnitName used to filter the
     * [CartographicBoundary]
     * @return a Flow of CartographicBoundary objects (can be null initially) that might emit
     * updates in the future based on changes in the database.
     */
    fun selectCartographicBoundaryByAdministrativeUnitName(
        administrativeUnitName: AdministrativeUnitName
    ): Flow<CartographicBoundary?> {
        return cartographicBoundaryDAO
            .selectCartographicBoundaryWithRegionsByAdministrativeUnitName(
                administrativeUnitNameID = administrativeUnitName.id
            )
            .map { it?.toCartographicBoundary() }
    }

    /**
     * Inserts a CartographicBoundary object into the database.
     *
     * This method delegates the insert operation to the `cartographicBoundaryDAO.insert` method.
     * Please refer to the documentation for `cartographicBoundaryDAO.insert` for details on
     * transaction handling, potential error conditions, and any basic data validation performed
     * on the CartographicBoundary object before insertion.
     *
     * @param cartographicBoundary the CartographicBoundary object to insert.
     */
    override suspend fun create(cartographicBoundary: CartographicBoundary) {
        cartographicBoundaryDAO.insert(cartographicBoundary = cartographicBoundary)
    }

    /**
     * Updates a [CartographicBoundary] object in the database.
     *
     * This method delegates the update operation to the `cartographicBoundaryDAO.update` method.
     * Please refer to the documentation for `cartographicBoundaryDAO.update` for details on
     * transaction handling, potential error conditions, and any basic data validation performed
     * on the CartographicBoundary object before update.
     *
     * @param cartographicBoundary the [CartographicBoundary] object containing the updated data.
     */
    override suspend fun update(cartographicBoundary: CartographicBoundary) {
        cartographicBoundaryDAO.update(cartographicBoundary = cartographicBoundary)
    }
}