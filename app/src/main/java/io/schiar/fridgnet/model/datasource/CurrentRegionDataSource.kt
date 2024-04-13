package io.schiar.fridgnet.model.datasource

import io.schiar.fridgnet.model.Region
import kotlinx.coroutines.flow.Flow

/**
 * Interface representing a data source for managing the currently selected region on a map.
 * This data source might use in-memory storage, user preferences, or another mechanism to track
 * the current region selection.
 */
interface CurrentRegionDataSource {
    /**
     * Retrieves the currently selected [Region] from the data source as a stream of updates using
     * Kotlin's Flow API. The Flow will emit a new value whenever the selected region changes, or
     * null if there is no currently selected region.
     *
     * @return a Flow of the currently selected [Region] which could be null.
     */
    fun retrieve(): Flow<Region?>

    /**
     * Updates the currently selected region in the data source with the provided [Region] object.
     *
     * @param region the new [Region] to be selected.
     */
    fun update(region: Region)
}