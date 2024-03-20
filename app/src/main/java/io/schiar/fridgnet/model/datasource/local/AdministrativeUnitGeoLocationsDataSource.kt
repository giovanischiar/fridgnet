package io.schiar.fridgnet.model.datasource.local

import io.schiar.fridgnet.Log
import io.schiar.fridgnet.model.AdministrativeLevel
import io.schiar.fridgnet.model.AdministrativeUnit
import io.schiar.fridgnet.model.CartographicBoundary
import io.schiar.fridgnet.model.GeoLocation
import io.schiar.fridgnet.model.datasource.AdministrativeUnitDataSource
import io.schiar.fridgnet.model.datasource.retriever.AdministrativeUnitRetriever
import io.schiar.fridgnet.model.service.AdministrativeUnitService
import kotlinx.coroutines.flow.Flow

class AdministrativeUnitGeoLocationsDataSource(
    private val administrativeUnitRetriever: AdministrativeUnitRetriever,
    private val administrativeUnitService: AdministrativeUnitService
): AdministrativeUnitDataSource {
    override suspend fun create(geoLocation: GeoLocation, administrativeUnit: AdministrativeUnit) {
        log(geoLocation, "creating administrativeUnit ${administrativeUnit.name()}")
        administrativeUnitService.create(
            geoLocation = geoLocation, administrativeUnit = administrativeUnit
        )
    }

    override suspend fun retrieveAdministrativeUnitFor(geoLocation: GeoLocation) {
        log(geoLocation = geoLocation, "It's not on memory, retrieving using the Geocoder")
        val administrativeUnitFromRetriever = administrativeUnitRetriever.retrieve(
            geoLocation = geoLocation
        )
        if (administrativeUnitFromRetriever != null) {
            create(geoLocation = geoLocation, administrativeUnit = administrativeUnitFromRetriever)
            return
        }
        log(geoLocation = geoLocation, "It's not on the Geocoder!")
    }

    override fun retrieve(): Flow<List<Pair<AdministrativeUnit, List<CartographicBoundary>>>> {
        return administrativeUnitService.retrieve()
    }

    override fun retrieveGeoLocations(
        administrativeUnit: AdministrativeUnit, administrativeLevel: AdministrativeLevel
    ): Flow<List<GeoLocation>> {
        return administrativeUnitService.retrieveGeoLocations(
            administrativeUnit = administrativeUnit, administrativeLevel = administrativeLevel
        )
    }

    private fun log(geoLocation: GeoLocation, msg: String) {
        val (_, latitude, longitude) = geoLocation
        Log.d(
            tag = "GeoLocation to AdministrativeUnit Feature",
            msg = "Retrieving AdministrativeUnit for ($latitude, $longitude): $msg"
        )
    }
}