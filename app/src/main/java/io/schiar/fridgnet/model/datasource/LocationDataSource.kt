package io.schiar.fridgnet.model.datasource

import io.schiar.fridgnet.model.Location
import io.schiar.fridgnet.model.Region
import io.schiar.fridgnet.model.datasource.retriever.LocationRetriever

interface LocationDataSource: LocationRetriever {
    suspend fun setup(onLoaded: (location: Location) -> Unit)
    fun insert(location: Location)
    fun updateLocationWithRegionSwitched(location: Location, region: Region)
    suspend fun updateLocationWithAllRegionsSwitched(location: Location)
}