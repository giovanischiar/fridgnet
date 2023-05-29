package io.schiar.fridgnet.model.repository

import io.schiar.fridgnet.model.Address
import io.schiar.fridgnet.model.AdministrativeUnit
import io.schiar.fridgnet.model.AdministrativeUnit.*
import io.schiar.fridgnet.model.Location
import io.schiar.fridgnet.model.repository.datasource.LocationDBDataSource
import io.schiar.fridgnet.model.repository.datasource.LocationDataSource
import io.schiar.fridgnet.model.repository.datasource.LocationNominatimAPIDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LocationAPIDBRepository : LocationRepository {
    private val locationAPIDataSource: LocationDataSource = LocationNominatimAPIDataSource()
    private val locationDBDataSource: LocationDataSource = LocationDBDataSource()

    private var onLocationReady: (location: Location) -> Unit = {}

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
                            .store(address = address, location = location)
                    }
                }
                onLocationReady(location)
            }
        }
    }
}