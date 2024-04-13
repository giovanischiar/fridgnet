package io.schiar.fridgnet.model.datasource.local

import io.schiar.fridgnet.model.Region
import io.schiar.fridgnet.model.datasource.CurrentRegionDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

/**
 * Local data source implementation for managing the currently selected region on the regions and
 * images screen. This class uses a MutableStateFlow to store the current region and provides
 * methods to update and retrieve the selected region information.
 */
class CurrentRegionLocalDataSource @Inject constructor(): CurrentRegionDataSource {
    private val currentRegion = MutableStateFlow<Region?>(value = null)

    /**
     * Update the current [Region]
     *
     * @param region the region to update
     */
    override fun update(region: Region) {
        currentRegion.update { region }
    }

    /**
     * Retrieve the flow of [Region]
     *
     * @return the [Flow] of the current [Region]
     */
    override fun retrieve(): Flow<Region?> {
        return currentRegion
    }
}