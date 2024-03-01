package io.schiar.fridgnet.model.datasource

import io.schiar.fridgnet.model.Location
import io.schiar.fridgnet.model.Region
import io.schiar.fridgnet.model.datasource.retriever.LocationRetriever
import kotlinx.coroutines.flow.Flow

interface LocationDataSource: LocationRetriever {
    fun retrieve(): Flow<List<Location>>
    suspend fun create(location: Location)
    suspend fun updateWithRegionSwitched(location: Location, region: Region)
    suspend fun updateWithAllRegionsSwitched(location: Location)
}