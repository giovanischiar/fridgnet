package io.schiar.fridgnet.model.datasource

import io.schiar.fridgnet.model.Region
import kotlinx.coroutines.flow.Flow

interface CurrentRegionDataSource {
    fun retrieve(): Flow<Region?>
    fun update(region: Region)
}