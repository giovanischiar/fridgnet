package io.schiar.fridgnet.model.repository.location

import io.schiar.fridgnet.Log
import io.schiar.fridgnet.model.Address
import io.schiar.fridgnet.model.AdministrativeUnit.CITY
import io.schiar.fridgnet.model.BoundingBox
import io.schiar.fridgnet.model.Location
import io.schiar.fridgnet.model.Region
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Collections.synchronizedMap as syncMapOf

class LocationAPIDBRepository(
    private val locationAPIDataSource: LocationDataSource = LocationAPIDataSource(),
    private val locationDBDataSource: LocationDBDataSource
) : LocationRepository {
    override var allCitiesBoundingBox: BoundingBox? = null
    override var currentLocation: Location? = null
    private val regionLocation: MutableMap<Region, Location> = syncMapOf(mutableMapOf())
    private val cityAddressLocation: MutableMap<Address, Location> = syncMapOf(mutableMapOf())
    private var locationsBeingFetched: Set<Address> = emptySet()
    private var addressLocation: Map<Address, Location> = emptyMap()

    private var onLocationReady: (location: Location) -> Unit = {}

    override suspend fun setup() {
        locationDBDataSource.setup(onLoaded = ::onLoaded)
    }

    private fun log(address: Address, msg: String) {
        Log.d("Add Regions Feature", "Fetching ${address.name()}: $msg")
    }

    override fun regionsThatIntersect(boundingBox: BoundingBox): List<Region> {
        return regionLocation.keys.filter { region ->
            boundingBox.contains(other = region.boundingBox)
        }
    }

    override fun selectNewLocationFrom(region: Region) {
        currentLocation = regionLocation[region]
    }

    override fun cityAddressNameLocation(): Map<String, Location> {
        return cityAddressLocation.mapKeys { it.key.name() }
    }

    override suspend fun switchAll() {
        val currentLocation = this.currentLocation ?: return
        val locationUpdated = currentLocation.switchAll()
        with (regionLocation.iterator()) {
            forEach { if (currentLocation.regions.contains(it.key)) remove() }
        }
        addRegionLocation(location = locationUpdated)
        (locationDBDataSource as LocationDBDataSource).updateLocationWithAllRegionsSwitched(
            location = locationUpdated
        )
        this.currentLocation = locationUpdated
        onLocationReady(locationUpdated)
    }

    override suspend fun switchRegion(region: Region) {
        val location = regionLocation[region] ?: return
        log(location.address, "Switching region")
        val locationUpdated = location.switch(region = region)
        addRegionLocation(location = locationUpdated)
        regionLocation.remove(region)
        regionLocation[region.switch()] = locationUpdated
        (locationDBDataSource as LocationDBDataSource).updateLocationWithRegionSwitched(
            location = locationUpdated,
            region = region
        )
        currentLocation = locationUpdated
        onLocationReady(locationUpdated)
    }

    private fun onLoaded(location: Location) {
        log(address = location.address, "Loading location from database")
        addressLocation = addressLocation + (location.address to location)
    }

    override suspend fun loadRegions(address: Address, onLocationReady: (location: Location) -> Unit) {
        this.onLocationReady = onLocationReady
        val locationAlreadyBeingFetched = synchronized(lock = this) {
            locationsBeingFetched.contains(address)
        }

        if (!locationAlreadyBeingFetched) {
            locationsBeingFetched = locationsBeingFetched + address
            coroutineScope {
                launch(Dispatchers.IO) {
                    log(address,"Job started")
                    val location = withContext(Dispatchers.IO) {
                        fetchLocationBy(address = address)
                    }
                    if (location != null) {
                        onLocationReady(location)
                        addRegionLocation(location = location)
                    }
                }
            }
        }
    }

    private suspend fun addRegionLocation(location: Location) = coroutineScope {
        if (location.address.administrativeUnit == CITY) {
            log(location.address, "Location is a city! Add to cityAddressLocation map")
            cityAddressLocation[location.address] = location
            launch {
                synchronized(this) {
                    allCitiesBoundingBox = if (allCitiesBoundingBox == null) {
                        location.boundingBox
                    } else {
                        allCitiesBoundingBox!! + location.boundingBox
                    }
                }
            }
        }

        log(location.address, "Going through the ${location.regions.size} regions of the location to add")
        for (region in location.regions) {
            regionLocation[region] = location
        }
        log(location.address, "${location.regions.size} regions of the location just added!")
    }

    private suspend fun fetchLocationBy(address: Address): Location? {
        log(address = address, "Let's check on the memory")
        return if (addressLocation.containsKey(address)) {
            log(address = address, "It's already on the memory! Returning...")
            addressLocation[address]
        } else {
            log(address = address, "Shoot! Time to search in the database")
            val locationFromDatabase = withContext(Dispatchers.IO) {
                locationDBDataSource.fetchLocationBy(address = address)
            }
            if (locationFromDatabase != null) {
                log(address = address, "it's on the database! Returning...")
                onLoaded(location = locationFromDatabase)
                locationFromDatabase
            } else {
                log(address = address, "Shoot! Time to search in the API")
                val locationFromAPI = withContext(Dispatchers.IO) {
                    locationAPIDataSource.fetchLocationBy(address = address)
                }
                if (locationFromAPI != null) {
                    log(address = address, "It's on the API! Returning...")
                    onLoaded(location = locationFromAPI)
                    coroutineScope {
                        launch(Dispatchers.IO) {
                            locationDBDataSource.insert(location = locationFromAPI)
                        }
                    }
                }
                locationFromAPI
            }
        }
    }
}