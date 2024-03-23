package io.schiar.fridgnet.model.datasource.retriever

import io.schiar.fridgnet.model.AdministrativeUnitName
import io.schiar.fridgnet.model.GeoLocation
import kotlinx.coroutines.flow.Flow

interface AdministrativeUnitNameRetriever {
    fun retrieve(geoLocations: List<GeoLocation>): Flow<Pair<GeoLocation, AdministrativeUnitName>>
}