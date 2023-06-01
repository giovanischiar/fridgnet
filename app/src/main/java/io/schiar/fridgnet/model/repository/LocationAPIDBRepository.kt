package io.schiar.fridgnet.model.repository

import io.schiar.fridgnet.model.Address
import io.schiar.fridgnet.model.AdministrativeUnit
import io.schiar.fridgnet.model.AdministrativeUnit.*
import io.schiar.fridgnet.model.Location
import io.schiar.fridgnet.model.repository.datasource.LocationAPIDataSource
import io.schiar.fridgnet.model.repository.datasource.LocationDBDataSource
import io.schiar.fridgnet.model.repository.datasource.LocationDataSource
import io.schiar.fridgnet.model.repository.datasource.room.LocationDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LocationAPIDBRepository(locationDatabase: LocationDatabase) : LocationRepository {
    private val locationAPIDataSource: LocationDataSource = LocationAPIDataSource()
    private val locationDBDataSource: LocationDataSource = LocationDBDataSource(
        locationDatabase = locationDatabase
    )
    private var onLocationReady: (location: Location) -> Unit = {}

    override suspend fun setup() { (locationDBDataSource as LocationDBDataSource).setup() }
    override suspend fun fetch(address: Address, onLocationReady: (location: Location) -> Unit) {
        this.onLocationReady = onLocationReady
        val addresses = address.allAddresses()
        val (city, county, state, country) = addresses
        fetchLocation(address = city, administrativeUnit = CITY)
        fetchLocation(address = county, administrativeUnit = COUNTY)
        fetchLocation(address = state, administrativeUnit = STATE)
        fetchLocation(address = country, administrativeUnit = COUNTRY)
    }

    private suspend fun fetchLocation(address: Address, administrativeUnit: AdministrativeUnit) {
        coroutineScope {
            launch {
                var location = withContext(Dispatchers.IO) {
                    when (administrativeUnit) {
                        CITY -> locationDBDataSource.fetchCity(address = address)
                        COUNTY -> locationDBDataSource.fetchCounty(address = address)
                        STATE -> locationDBDataSource.fetchState(address = address)
                        COUNTRY -> locationDBDataSource.fetchCountry(address = address)
                    }
                }

                if (location == null) {
                    val apiFetchedLocation = withContext(Dispatchers.IO) {
                        when (administrativeUnit) {
                            CITY -> locationAPIDataSource.fetchCity(address = address)
                            COUNTY -> locationAPIDataSource.fetchCounty(address = address)
                            STATE -> locationAPIDataSource.fetchState(address = address)
                            COUNTRY -> locationAPIDataSource.fetchCountry(address = address)
                        }

                    }
                    location = apiFetchedLocation ?: return@launch

                    launch {
                        (locationDBDataSource as LocationDBDataSource)
                            .insert(location = location)
                    }
                }
                onLocationReady(location)
            }
        }
    }
}