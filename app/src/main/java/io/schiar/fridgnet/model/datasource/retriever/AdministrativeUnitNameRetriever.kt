package io.schiar.fridgnet.model.datasource.retriever

import io.schiar.fridgnet.model.AdministrativeUnitName
import io.schiar.fridgnet.model.GeoLocation

interface AdministrativeUnitNameRetriever {
    suspend fun retrieve(geoLocation: GeoLocation): AdministrativeUnitName?
}