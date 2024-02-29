package io.schiar.fridgnet.model.repository.location

import io.schiar.fridgnet.Log
import io.schiar.fridgnet.model.Address
import io.schiar.fridgnet.model.AdministrativeUnit.CITY
import io.schiar.fridgnet.model.BoundingBox
import io.schiar.fridgnet.model.Location
import io.schiar.fridgnet.model.Region
import io.schiar.fridgnet.model.datasource.LocationDataSource
import io.schiar.fridgnet.model.datasource.retriever.LocationRetriever
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Collections.synchronizedMap as syncMapOf

class LocationAPIDBRepository(
    private val locationRetriever: LocationRetriever,
    private val locationDataSource: LocationDataSource
) : LocationRepository {
    override var allCitiesBoundingBox: BoundingBox? = null
    override var currentLocation: Location? = null
    private val regionLocation: MutableMap<Region, Location> = syncMapOf(mutableMapOf())
    private val cityAddressLocation: MutableMap<Address, Location> = syncMapOf(mutableMapOf())
    private var locationsBeingFetched: Set<Address> = emptySet()
    private var addressLocation: Map<Address, Location> = emptyMap()
    override val locationAddress: MutableMap<Address, Location> = syncMapOf(mutableMapOf())

    private var onLocationReady: suspend (location: Location) -> Unit = {}

    override suspend fun setup() {
        locationDataSource.setup(onLoaded = ::onLoaded)
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
        with(regionLocation.iterator()) {
            forEach { if (currentLocation.regions.contains(it.key)) remove() }
        }
        addRegionLocation(location = locationUpdated)
        locationDataSource.updateWithAllRegionsSwitched(
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
        locationDataSource.updateWithRegionSwitched(
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

    override suspend fun loadRegions(
        address: Address, onLocationReady: suspend (location: Location) -> Unit
    ) {
        this.onLocationReady = onLocationReady
        val locationAlreadyBeingFetched = synchronized(lock = this) {
            locationsBeingFetched.contains(address)
        }

        if (!locationAlreadyBeingFetched) {
            locationsBeingFetched = locationsBeingFetched + address
            coroutineScope {
                launch(Dispatchers.IO) {
                    log(address, "Job started")
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

        log(
            location.address,
            "Going through the ${location.regions.size} regions of the location to add"
        )
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
            val locationFromDataSource = withContext(Dispatchers.IO) {
                locationDataSource.retrieve(address = address)
            }
            if (locationFromDataSource != null) {
                log(address = address, "it's on the database! Returning...")
                onLoaded(location = locationFromDataSource)
                locationFromDataSource
            } else {
                log(address = address, "Shoot! Time to search in the API")
                val locationFromRetriever = withContext(Dispatchers.IO) {
                    locationRetriever.retrieve(address = address)
                }
                if (locationFromRetriever != null) {
                    log(address = address, "It's on the API! Returning...")
                    onLoaded(location = locationFromRetriever)
                    coroutineScope {
                        launch(Dispatchers.IO) {
                            locationDataSource.create(location = locationFromRetriever)
                        }
                    }
                }
                locationFromRetriever
            }
        }
    }
}