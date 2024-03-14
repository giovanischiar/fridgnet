package io.schiar.fridgnet.model.datasource.local

import io.schiar.fridgnet.model.Region
import io.schiar.fridgnet.model.datasource.CurrentRegionDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

class CurrentRegionLocalDataSource: CurrentRegionDataSource {
    private var _currentRegion: Region? = null
    private val currentRegion = MutableStateFlow(_currentRegion)

    override fun update(region: Region) {
        _currentRegion = region
        currentRegion.update { region }
    }

    override fun retrieve(): Flow<Region?> {
        return currentRegion
    }
}