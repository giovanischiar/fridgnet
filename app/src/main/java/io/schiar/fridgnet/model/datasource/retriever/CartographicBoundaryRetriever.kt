package io.schiar.fridgnet.model.datasource.retriever

import io.schiar.fridgnet.model.AdministrativeLevel
import io.schiar.fridgnet.model.AdministrativeUnitName
import io.schiar.fridgnet.model.CartographicBoundary
import kotlinx.coroutines.flow.Flow

/**
 * Interface representing a retriever for cartographic boundaries.
 */
interface CartographicBoundaryRetriever {
    /**
     * Retrieves a stream ([Flow]) of [CartographicBoundary] objects asynchronously based on a
     * provided list of administrative unit criteria. Each element in the list is a pair consisting
     * of an [AdministrativeLevel] and an [AdministrativeUnitName]. This method retrieves boundaries
     * that exactly match the specified level and potentially some or all name components of the
     * provided [AdministrativeUnitName] object.
     *
     * @param administrativeUnitLevelAndAdministrativeUnitNameList a [List] of each
     * [AdministrativeLevel] paired with its [AdministrativeUnitName]
     * @return            the [Flow] of [CartographicBoundary]. Each time a
     * [CartographicBoundary] is found from the
     * [administrativeUnitLevelAndAdministrativeUnitNameList] this flow will emit the
     * [CartographicBoundary] retrieved
     */
    fun retrieve(
        administrativeUnitLevelAndAdministrativeUnitNameList:
            List<Pair<AdministrativeLevel, AdministrativeUnitName>>
    ): Flow<CartographicBoundary>
}