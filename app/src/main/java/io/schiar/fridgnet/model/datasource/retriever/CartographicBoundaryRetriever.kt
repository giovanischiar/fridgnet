package io.schiar.fridgnet.model.datasource.retriever

import io.schiar.fridgnet.model.AdministrativeUnit
import io.schiar.fridgnet.model.CartographicBoundary

interface CartographicBoundaryRetriever {
    suspend fun retrieveLocality(administrativeUnit: AdministrativeUnit): CartographicBoundary?
    suspend fun retrieveSubAdmin(administrativeUnit: AdministrativeUnit): CartographicBoundary?
    suspend fun retrieveAdmin(administrativeUnit: AdministrativeUnit): CartographicBoundary?
    suspend fun retrieveCountry(administrativeUnit: AdministrativeUnit): CartographicBoundary?
}