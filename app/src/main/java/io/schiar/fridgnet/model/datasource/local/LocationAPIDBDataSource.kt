package io.schiar.fridgnet.model.datasource.local

import io.schiar.fridgnet.Log
import io.schiar.fridgnet.model.Address
import io.schiar.fridgnet.model.AdministrativeUnit
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
    private val addressSet: MutableSet<String> = syncSetOf(mutableSetOf())
    private val addressLocationCache: MutableMap<Address, Location> = syncMapOf(mutableMapOf())
    private val locationsCacheFlow: MutableStateFlow<List<Location>> = MutableStateFlow(
        value = addressLocationCache.values.toList()
    )

    private suspend fun create(location: Location) {
        locationsCacheFlow.update { addressLocationCache.values.toList() }
        locationService.create(location = location)
    }

    private fun updateCache(address: Address, location: Location) {
        addressLocationCache[address] = location
    }

    private fun updateCache(locations: List<Location>) {
        locations.forEach { updateCache(address = it.address, location = it) }
    }

    override suspend fun createFrom(address: Address, administrativeUnit: AdministrativeUnit) {
        val addressAdministrativeUnit = Pair(address, administrativeUnit)
        val addressName = address.name(administrativeUnit = administrativeUnit)
        if (addressSet.contains(element = addressName)) return
        addressSet.add(element = addressName)
        log(addressAdministrativeUnit = addressAdministrativeUnit, "It's not on memory, retrieving using the API")
        val locationFromRetriever = when(administrativeUnit) {
            AdministrativeUnit.CITY -> locationRetriever.retrieveLocality(address = address)
            AdministrativeUnit.COUNTY -> locationRetriever.retrieveSubAdmin(address = address)
            AdministrativeUnit.STATE -> locationRetriever.retrieveAdmin(address = address)
            AdministrativeUnit.COUNTRY -> locationRetriever.retrieveCountry(address = address)
        }
        if (locationFromRetriever != null) {
            create(location = locationFromRetriever)
            return
        }
        log(addressAdministrativeUnit = addressAdministrativeUnit, "It's not on the API!")
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

    private fun log(addressAdministrativeUnit: Pair<Address, AdministrativeUnit>, msg: String) {
        val (address, administrativeUnit) = addressAdministrativeUnit
        Log.d(
            "Address to Location Feature",
            "Retrieving Location for ${address.name(administrativeUnit = administrativeUnit)}: $msg"
        )
    }
}