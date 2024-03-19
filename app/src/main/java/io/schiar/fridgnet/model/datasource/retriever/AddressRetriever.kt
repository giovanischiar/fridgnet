package io.schiar.fridgnet.model.datasource.retriever

import io.schiar.fridgnet.model.Address
import io.schiar.fridgnet.model.GeoLocation

interface AddressRetriever {
    suspend fun retrieve(geoLocation: GeoLocation): Address?
}