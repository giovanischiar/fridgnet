package io.schiar.fridgnet.model.repository.datasource

import io.schiar.fridgnet.model.Address
import io.schiar.fridgnet.model.Location

class LocationDBDataSource: LocationDataSource {
    private var locations: Map<Address, Location> = emptyMap()

    override suspend fun fetchCity(address: Address): Location? {
        return locations[address]
    }

    override suspend fun fetchCounty(address: Address): Location? {
        return locations[address]
    }

    override suspend fun fetchState(address: Address): Location? {
        return locations[address]
    }

    override suspend fun fetchCountry(address: Address): Location? {
        return locations[address]
    }

    fun store(address: Address, location: Location) {
        locations = locations + (address to location)
    }
}