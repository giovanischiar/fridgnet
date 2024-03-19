package io.schiar.fridgnet.model.datasource.retriever

import io.schiar.fridgnet.model.AdministrativeUnit
import io.schiar.fridgnet.model.GeoLocation

interface AdministrativeUnitRetriever {
    suspend fun retrieve(geoLocation: GeoLocation): AdministrativeUnit?
}