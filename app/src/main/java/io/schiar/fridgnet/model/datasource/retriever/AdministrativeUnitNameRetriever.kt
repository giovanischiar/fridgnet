package io.schiar.fridgnet.model.datasource.retriever

import io.schiar.fridgnet.model.AdministrativeUnitName
import io.schiar.fridgnet.model.GeoLocation
import kotlinx.coroutines.flow.Flow

/**
 * Interface representing a retriever for administrative unit names.
 */
interface AdministrativeUnitNameRetriever {
    /**
     * Retrieves a stream ([Flow]) of [AdministrativeUnitName] objects asynchronously based on the
     * provided  [GeoLocation]. The Flow will emit one [AdministrativeUnitName] for each
     * [GeoLocation] input
     *
     * @param geoLocation the geographic location for which to retrieve administrative unit names.
     * @return a Flow of [AdministrativeUnitName] objects associated with the provided location.
     */
    fun retrieve(geoLocation: GeoLocation): Flow<AdministrativeUnitName>
}