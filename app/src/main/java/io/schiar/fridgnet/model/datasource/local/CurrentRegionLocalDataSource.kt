package io.schiar.fridgnet.model.datasource.local

import io.schiar.fridgnet.model.Region
import io.schiar.fridgnet.model.datasource.CurrentRegionDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

class CurrentRegionLocalDataSource @Inject constructor(): CurrentRegionDataSource {
    private val currentRegion = MutableStateFlow<Region?>(value = null)

    override fun update(region: Region) {
        currentRegion.update { region }
    }

    override fun retrieve(): Flow<Region?> {
        return currentRegion
    }
}