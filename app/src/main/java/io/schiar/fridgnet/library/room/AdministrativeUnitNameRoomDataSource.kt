package io.schiar.fridgnet.library.room

import io.schiar.fridgnet.model.AdministrativeUnitName
import io.schiar.fridgnet.model.CartographicBoundary
import io.schiar.fridgnet.model.GeoLocation
import io.schiar.fridgnet.model.datasource.AdministrativeUnitNameDataSource
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

/**
 * Implementation of [AdministrativeUnitNameDataSource] that utilizes Room Database for CRUD
 * (Create, Read, Update, Delete) operations on [AdministrativeUnitName] data. This class also
 * handles the association with [GeoLocation] and retrieves [CartographicBoundary] data for existing
 * [AdministrativeUnitName]s.
 *
 * It leverages dependency injection to receive an instance of [AdministrativeUnitNameDAO]
 * for interacting with the database layer.
 */
class AdministrativeUnitNameRoomDataSource @Inject constructor(
    private val administrativeUnitNameDAO: AdministrativeUnitNameDAO
) : AdministrativeUnitNameDataSource {
    private val administrativeUnitNameIDS = mutableSetOf<Long>()

    /**
     * Create a [AdministrativeUnitName] with its associated [GeoLocation]
     *
     * @param geoLocation the [GeoLocation] used to associate
     * @param administrativeUnitName the [AdministrativeUnitName] used to create (or update)
     */
    override suspend fun create(
        geoLocation: GeoLocation, administrativeUnitName: AdministrativeUnitName
    ) {
        administrativeUnitNameDAO.insert(
            geoLocation = geoLocation,
            administrativeUnitName = administrativeUnitName
        )
    }

    /**
     * Retrieves a Flow of [Pair]<[AdministrativeUnitName], [List]<[CartographicBoundary]>>.
     *
     * This method retrieves [AdministrativeUnitName] data along with any existing associated
     * [CartographicBoundary] entities. It utilizes [Flow] to emit data asynchronously.
     *
     * To avoid emitting duplicate [AdministrativeUnitName] entries, it maintains a set of
     * IDs (administrativeUnitNameIDS). Only new (unseen) entries are emitted.
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    override fun retrieveAdministrativeUnitNameWithExistentCartographicBoundaries()
        : Flow<Pair<AdministrativeUnitName, List<CartographicBoundary>>> {
        return administrativeUnitNameDAO.selectAdministrativeUnitNameWithCartographicBoundaries()
            .flatMapLatest { administrativeUnitNameWithCartographicBoundariesList ->
                flow {
                    for (
                        administrativeUnitNameWithCartographicBoundaries in
                        administrativeUnitNameWithCartographicBoundariesList
                    ) {
                        val administrativeUnitNameID
                            = administrativeUnitNameWithCartographicBoundaries
                            .administrativeUnitNameEntity
                            .id
                        if (administrativeUnitNameIDS.add(element = administrativeUnitNameID)) {
                            emit(
                                administrativeUnitNameWithCartographicBoundaries
                                    .toAdministrativeUnitNameAndCartographicBoundaries()
                            )
                        }
                    }
                }
            }
    }
}