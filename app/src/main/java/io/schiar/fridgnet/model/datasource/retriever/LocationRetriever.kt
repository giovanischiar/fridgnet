package io.schiar.fridgnet.model.datasource.retriever

import io.schiar.fridgnet.model.AdministrativeUnit
import io.schiar.fridgnet.model.Location

interface LocationRetriever {
    suspend fun retrieveLocality(administrativeUnit: AdministrativeUnit): Location?
    suspend fun retrieveSubAdmin(administrativeUnit: AdministrativeUnit): Location?
    suspend fun retrieveAdmin(administrativeUnit: AdministrativeUnit): Location?
    suspend fun retrieveCountry(administrativeUnit: AdministrativeUnit): Location?
}