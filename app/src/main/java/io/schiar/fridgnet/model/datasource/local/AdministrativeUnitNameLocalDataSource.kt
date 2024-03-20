package io.schiar.fridgnet.model.datasource.local

import io.schiar.fridgnet.Log
import io.schiar.fridgnet.model.AdministrativeLevel
import io.schiar.fridgnet.model.AdministrativeUnitName
import io.schiar.fridgnet.model.CartographicBoundary
import io.schiar.fridgnet.model.GeoLocation
import io.schiar.fridgnet.model.datasource.AdministrativeUnitNameDataSource
import io.schiar.fridgnet.model.datasource.retriever.AdministrativeUnitNameRetriever
import io.schiar.fridgnet.model.service.AdministrativeUnitNameService
import kotlinx.coroutines.flow.Flow

class AdministrativeUnitNameLocalDataSource(
    private val administrativeUnitNameRetriever: AdministrativeUnitNameRetriever,
    private val administrativeUnitNameService: AdministrativeUnitNameService
): AdministrativeUnitNameDataSource {
    override suspend fun create(geoLocation: GeoLocation, administrativeUnitName: AdministrativeUnitName) {
        log(geoLocation, "creating administrativeUnitName ${administrativeUnitName.name()}")
        administrativeUnitNameService.create(
            geoLocation = geoLocation, administrativeUnitName = administrativeUnitName
        )
    }

    override suspend fun retrieveAdministrativeUnitNameFor(geoLocation: GeoLocation) {
        log(geoLocation = geoLocation, "It's not on memory, retrieving using the Geocoder")
        val administrativeUnitNameFromRetriever = administrativeUnitNameRetriever.retrieve(
            geoLocation = geoLocation
        )
        if (administrativeUnitNameFromRetriever != null) {
            create(geoLocation = geoLocation, administrativeUnitName = administrativeUnitNameFromRetriever)
            return
        }
        log(geoLocation = geoLocation, "It's not on the Geocoder!")
    }

    override fun retrieve(): Flow<List<Pair<AdministrativeUnitName, List<CartographicBoundary>>>> {
        return administrativeUnitNameService.retrieve()
    }

    override fun retrieveGeoLocations(
        administrativeUnitName: AdministrativeUnitName, administrativeLevel: AdministrativeLevel
    ): Flow<List<GeoLocation>> {
        return administrativeUnitNameService.retrieveGeoLocations(
            administrativeUnitName = administrativeUnitName, administrativeLevel = administrativeLevel
        )
    }

    private fun log(geoLocation: GeoLocation, msg: String) {
        val (_, latitude, longitude) = geoLocation
        Log.d(
            tag = "GeoLocation to AdministrativeUnitName Feature",
            msg = "Retrieving AdministrativeUnitName for ($latitude, $longitude): $msg"
        )
    }
}