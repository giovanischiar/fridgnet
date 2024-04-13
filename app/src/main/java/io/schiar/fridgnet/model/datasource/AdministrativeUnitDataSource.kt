package io.schiar.fridgnet.model.datasource

import io.schiar.fridgnet.model.AdministrativeLevel
import io.schiar.fridgnet.model.AdministrativeUnit
import io.schiar.fridgnet.model.BoundingBox
import io.schiar.fridgnet.model.Region
import kotlinx.coroutines.flow.Flow

/**
 * Interface representing a data source for managing [AdministrativeUnit] objects. This data source
 * could be local storage, a remote server, or another solution for storing administrative unit
 * data.
 */
interface AdministrativeUnitDataSource {
    /**
     * Retrieves a stream ([Flow]) of all [AdministrativeUnit] objects from the data source.
     *
     * @return a Flow of a list containing all [AdministrativeUnit] objects.
     */
    fun retrieve(): Flow<List<AdministrativeUnit>>

    /**
     * Retrieves a stream (Flow) of [AdministrativeUnit] objects filtered by the provided
     * [administrativeLevel].
     *
     * @param administrativeLevel the level (e.g., state, county, city) used to filter the units.
     * @return a Flow of a list containing [AdministrativeUnit] objects matching the administrative
     * level.
     */
    fun retrieve(administrativeLevel: AdministrativeLevel): Flow<List<AdministrativeUnit>>

    /**
     * Retrieves a stream (Flow) of [Region] objects that are located entirely within the specified
     * [boundingBox]. By default, this method retrieves active regions, however the definition of
     * "active" might depend on the specific implementation.
     *
     * @param boundingBox the bounding box used to filter the regions.
     * @return            a [Flow] of a list containing [Region] objects within the bounding box.
     */
    fun retrieveActiveRegionsWithin(boundingBox: BoundingBox): Flow<List<Region>>

    /**
     * Retrieves a stream ([Flow]) of the currently selected [AdministrativeUnit] object. The [Flow]
     * will emit whenever the selected unit changes.
     *
     * @return a Flow of the currently selected [AdministrativeUnit] object.
     */
    fun retrieveCurrent(): Flow<AdministrativeUnit>

    /**
     * Updates the currently selected `AdministrativeUnit` based on the provided index. The behavior
     * depends on the implementation, but it's typically assumed that the index corresponds to the
     * order in which units were retrieved using methods like `retrieve` or `retrieveByLevel`.
     *
     * @param index the index of the new selected unit.
     */
    fun updateCurrentIndex(index: Int)
}