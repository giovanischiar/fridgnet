package io.schiar.fridgnet.model.datasource.retriever

import io.schiar.fridgnet.model.AdministrativeLevel
import io.schiar.fridgnet.model.AdministrativeUnitName
import io.schiar.fridgnet.model.CartographicBoundary
import kotlinx.coroutines.flow.Flow

interface CartographicBoundaryRetriever {
    fun retrieve(
        administrativeUnitLevelAndAdministrativeUnitNameList:
            List<Pair<AdministrativeLevel, AdministrativeUnitName>>
    ): Flow<CartographicBoundary>
}