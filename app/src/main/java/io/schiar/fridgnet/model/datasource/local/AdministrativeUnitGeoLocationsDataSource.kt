package io.schiar.fridgnet.model.datasource.local

import io.schiar.fridgnet.Log
import io.schiar.fridgnet.model.AdministrativeLevel
import io.schiar.fridgnet.model.AdministrativeUnit
import io.schiar.fridgnet.model.AdministrativeUnitCartographicBoundariesGeoLocations
import io.schiar.fridgnet.model.GeoLocation
import io.schiar.fridgnet.model.datasource.AdministrativeUnitDataSource
import io.schiar.fridgnet.model.datasource.retriever.AdministrativeUnitRetriever
import io.schiar.fridgnet.model.service.AdministrativeUnitService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.onEach
import java.util.Collections.synchronizedSet as syncSetOf

class AdministrativeUnitGeoLocationsDataSource(
    private val administrativeUnitRetriever: AdministrativeUnitRetriever,
    private val administrativeUnitService: AdministrativeUnitService
): AdministrativeUnitDataSource {
    private val geoLocationSet = syncSetOf(mutableSetOf<GeoLocation>())

    override suspend fun create(geoLocation: GeoLocation, administrativeUnit: AdministrativeUnit) {
        log(geoLocation, "creating administrativeUnit ${administrativeUnit.name()}")
        administrativeUnitService.create(geoLocation = geoLocation, administrativeUnit = administrativeUnit)
    }

    private fun updateCacheFromService(
        administrativeUnitesLocationsGeoLocations: List<AdministrativeUnitCartographicBoundariesGeoLocations>
    ) {
        administrativeUnitesLocationsGeoLocations.forEach { administrativeUnitesLocationsGeoLocation ->
            geoLocationSet.addAll(elements = administrativeUnitesLocationsGeoLocation.geoLocations)
        }
    }

    override suspend fun retrieveAdministrativeUnitFor(geoLocation: GeoLocation) {
        if (geoLocationSet.contains(element = geoLocation)) return
        geoLocationSet.add(element = geoLocation)
        log(geoLocation = geoLocation, "It's not on memory, retrieving using the Geocoder")
        val administrativeUnitFromRetriever = administrativeUnitRetriever.retrieve(geoLocation = geoLocation)
        if (administrativeUnitFromRetriever != null) {
            create(geoLocation = geoLocation, administrativeUnit = administrativeUnitFromRetriever)
            return
        }
        log(geoLocation = geoLocation, "It's not on the Geocoder!")
    }

    override fun retrieve(): Flow<List<AdministrativeUnitCartographicBoundariesGeoLocations>> {
        return administrativeUnitService.retrieve().onEach(::updateCacheFromService)
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