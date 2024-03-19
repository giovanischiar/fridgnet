package io.schiar.fridgnet.model.datasource.local

import io.schiar.fridgnet.Log
import io.schiar.fridgnet.model.AdministrativeUnit
import io.schiar.fridgnet.model.AdministrativeLevel
import io.schiar.fridgnet.model.Location
import io.schiar.fridgnet.model.Region
import io.schiar.fridgnet.model.datasource.LocationDataSource
import io.schiar.fridgnet.model.datasource.retriever.LocationRetriever
import io.schiar.fridgnet.model.service.LocationService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import java.util.Collections.synchronizedMap as syncMapOf
import java.util.Collections.synchronizedSet as syncSetOf

class LocationAPIDBDataSource(
    private val locationRetriever: LocationRetriever,
    private val locationService: LocationService
): LocationDataSource {
    private val administrativeUnitSet: MutableSet<String> = syncSetOf(mutableSetOf())
    private val administrativeUnitLocationCache: MutableMap<AdministrativeUnit, Location> = syncMapOf(mutableMapOf())
    private val locationsCacheFlow: MutableStateFlow<List<Location>> = MutableStateFlow(
        value = administrativeUnitLocationCache.values.toList()
    )

    private suspend fun create(location: Location) {
        locationsCacheFlow.update { administrativeUnitLocationCache.values.toList() }
        locationService.create(location = location)
    }

    private fun updateCache(administrativeUnit: AdministrativeUnit, location: Location) {
        administrativeUnitLocationCache[administrativeUnit] = location
    }

    private fun updateCache(locations: List<Location>) {
        locations.forEach { updateCache(administrativeUnit = it.administrativeUnit, location = it) }
    }

    override suspend fun retrieveLocationFor(administrativeUnit: AdministrativeUnit, administrativeLevel: AdministrativeLevel) {
        val administrativeUnitAdministrativeLevel = Pair(administrativeUnit, administrativeLevel)
        val administrativeUnitName = administrativeUnit.name(administrativeLevel = administrativeLevel)
        if (administrativeUnitSet.contains(element = administrativeUnitName)) return
        administrativeUnitSet.add(element = administrativeUnitName)
        log(administrativeUnitAdministrativeLevel = administrativeUnitAdministrativeLevel, "It's not on memory, retrieving using the API")
        val locationFromRetriever = when(administrativeLevel) {
            AdministrativeLevel.CITY -> locationRetriever.retrieveLocality(administrativeUnit = administrativeUnit)
            AdministrativeLevel.COUNTY -> locationRetriever.retrieveSubAdmin(administrativeUnit = administrativeUnit)
            AdministrativeLevel.STATE -> locationRetriever.retrieveAdmin(administrativeUnit = administrativeUnit)
            AdministrativeLevel.COUNTRY -> locationRetriever.retrieveCountry(administrativeUnit = administrativeUnit)
        }
        if (locationFromRetriever != null) {
            create(location = locationFromRetriever)
            return
        }
        log(administrativeUnitAdministrativeLevel = administrativeUnitAdministrativeLevel, "It's not on the API!")
    }

    override fun retrieve(): Flow<List<Location>> {
        return merge(
            locationsCacheFlow, locationService.retrieve().onEach(::updateCache)
        ).distinctUntilChanged()
    }

    override fun retrieve(region: Region): Flow<Location?> {
        return locationService.retrieve(region = region)
    }

    override fun retrieveRegions(): Flow<List<Region>> {
        return locationService.retrieveRegions()
    }

    override suspend fun update(location: Location) {
        locationService.update(location = location)
    }

    private fun log(administrativeUnitAdministrativeLevel: Pair<AdministrativeUnit, AdministrativeLevel>, msg: String) {
        val (administrativeUnit, administrativeLevel) = administrativeUnitAdministrativeLevel
        Log.d(
            "AdministrativeUnit to Location Feature",
            "Retrieving Location for ${administrativeUnit.name(administrativeLevel = administrativeLevel)}: $msg"
        )
    }
}