package io.schiar.fridgnet.model.datasource.retriever

import io.schiar.fridgnet.model.AdministrativeUnitName
import io.schiar.fridgnet.model.CartographicBoundary

interface CartographicBoundaryRetriever {
    suspend fun retrieveLocality(administrativeUnitName: AdministrativeUnitName): CartographicBoundary?
    suspend fun retrieveSubAdmin(administrativeUnitName: AdministrativeUnitName): CartographicBoundary?
    suspend fun retrieveAdmin(administrativeUnitName: AdministrativeUnitName): CartographicBoundary?
    suspend fun retrieveCountry(administrativeUnitName: AdministrativeUnitName): CartographicBoundary?
}