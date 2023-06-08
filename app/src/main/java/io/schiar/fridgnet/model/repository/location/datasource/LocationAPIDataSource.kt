package io.schiar.fridgnet.model.repository.location.datasource

import io.schiar.fridgnet.Log
import io.schiar.fridgnet.model.*
import io.schiar.fridgnet.model.AdministrativeUnit.*
import io.schiar.fridgnet.model.repository.location.datasource.nominatim.GeoJson
import io.schiar.fridgnet.model.repository.location.datasource.nominatim.GeoJsonAttributes
import io.schiar.fridgnet.model.repository.location.datasource.nominatim.PolygonSearcher
import io.schiar.fridgnet.viewmodel.util.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.withContext

class LocationAPIDataSource: LocationDataSource {
    private var fetchingPlaces: Set<String> = emptySet()
    private var mutex: Mutex = Mutex()

    override suspend fun fetchLocationBy(address: Address): Location? {
        return when (address.administrativeUnit) {
            CITY -> fetchCity(address = address)
            COUNTY -> fetchCounty(address = address)
            STATE -> fetchState(address = address)
            COUNTRY -> fetchCountry(address = address)
        }
    }

    private suspend fun fetchCity(address: Address): Location? {
        val newAddress = if (address.locality == null) {
            extractAddress(address = address)
        } else {
            address
        }
        val addressStr = newAddress.name()
        if (fetchingPlaces.contains(addressStr)) return null
        fetchingPlaces = fetchingPlaces + addressStr
        return fetchLocation(address = newAddress, administrativeUnit = CITY)
    }

    private suspend fun fetchCounty(address: Address): Location? {
        address.subAdminArea ?: return null
        address.adminArea ?: return null
        address.countryName ?: return null
        val countyAddressName = address.name()
        if (fetchingPlaces.contains(countyAddressName)) return null
        fetchingPlaces = fetchingPlaces + countyAddressName
        return fetchLocation(address = address, administrativeUnit = COUNTY)
    }

    private suspend fun fetchState(address: Address): Location? {
        address.adminArea ?: return null
        address.countryName ?: return null
        val stateAddressName = address.name()
        if (fetchingPlaces.contains(stateAddressName)) return null
        fetchingPlaces = fetchingPlaces + stateAddressName
        return fetchLocation(address = address, administrativeUnit = STATE)
    }

    private suspend fun fetchCountry(address: Address): Location? {
        address.countryName ?: return null
        val countryAddressName = address.name()
        if (fetchingPlaces.contains(countryAddressName)) return null
        fetchingPlaces = fetchingPlaces + countryAddressName
        return fetchLocation(address = address, administrativeUnit = COUNTRY)
    }

    private fun extractAddress(address: Address): Address {
        val name = address.name()
        val add = name.split(", ")
        if (add.size < 2) return address
        val state = add[1]
        val city = add[0]
        return Address(
            locality = city,
            subAdminArea = null,
            adminArea = state,
            countryName = address.countryName
        )
    }

    private suspend fun fetchLocation(
        address: Address,
        administrativeUnit: AdministrativeUnit
    ): Location? {
        val polygonSearcher = PolygonSearcher()
        val city = address.locality ?: ""
        val county = address.subAdminArea ?: ""
        val state = address.adminArea ?: ""
        val country = address.countryName ?: ""
        mutex.lock()
        val results = withContext(Dispatchers.IO) {
            when (administrativeUnit) {
                CITY -> {
                    polygonSearcher.searchCity(city = city, state = state, country = country)
                }

                COUNTY -> {
                    polygonSearcher.searchCounty(county = county, state = state, country = country)
                }

                STATE -> {
                    polygonSearcher.searchState(state = state, country = country)
                }

                COUNTRY -> {
                    polygonSearcher.searchCountry(country = country)
                }
            }
        }
        delay(1000) //Requests to Nominatim API should be limit to one per second
        mutex.unlock()
        val bodyList = results.body() ?: return null
        if (bodyList.isEmpty()) return null
        val body = bodyList[0]
        Log.d("API Result", "type: $administrativeUnit, address: ${address.name()} body.geojson: ${body.geojson}")
        val geoJson = body.geojson
        val boundingBox = body.boundingbox.toBoundingBox()
        return bodyToLocation(
            geoJson = geoJson,
            boundingBox = boundingBox,
            address = address,
            administrativeUnit = administrativeUnit
        )
    }

    private fun bodyToLocation(
        geoJson: GeoJson<GeoJsonAttributes>,
        boundingBox: BoundingBox,
        address: Address,
        administrativeUnit: AdministrativeUnit
    ): Location? {
        val regions = when (geoJson.type) {
            "Point" -> {
                val pointDoubleList = geoJson.coordinates as List<Double>
                val polygon = Polygon(coordinates = listOf(pointDoubleList.toCoordinate()))
                val region = Region(
                    polygon = polygon,
                    holes = emptyList(),
                    boundingBox = polygon.findBoundingBox(),
                    zIndex = administrativeUnit.zIndex()
                )
                listOf(region)
            }
            "LineString" -> {
                val pointDoubleList = geoJson.coordinates as List<List<Double>>
                val polygon = Polygon(coordinates = pointDoubleList.toLineStringCoordinates())
                val region = Region(
                    polygon = polygon,
                    holes = emptyList(),
                    boundingBox = polygon.findBoundingBox(),
                    zIndex = administrativeUnit.zIndex()
                )
                listOf(region)
            }

            "Polygon" -> {
                val pointDoubleList = geoJson.coordinates as List<List<List<Double>>>
                val polygonCoordinates = pointDoubleList.toPolygonCoordinates()
                val polygon = Polygon(coordinates = polygonCoordinates[0])
                val region = Region(
                    polygon = polygon,
                    holes = polygonCoordinates.subList(1, polygonCoordinates.size).map {
                        Polygon(coordinates = it)
                    },
                    boundingBox = polygon.findBoundingBox(),
                    zIndex = administrativeUnit.zIndex()
                )
                listOf(region)
            }

            "MultiPolygon" -> {
                val pointDoubleList = geoJson.coordinates as List<List<List<List<Double>>>>
                val regions = pointDoubleList.toMultiPolygonCoordinates().map {
                    val polygon = Polygon(coordinates = it[0])
                    Region(
                        polygon = polygon,
                        holes = it.subList(1, it.size).map { coordinates ->
                            Polygon(coordinates = coordinates)
                        },
                        boundingBox = polygon.findBoundingBox(),
                        zIndex = administrativeUnit.zIndex()
                    )
                }
                regions
            }

            else -> return null
        }

        return Location(
            address = address.addressAccordingTo(administrativeUnit),
            regions = regions,
            boundingBox = boundingBox,
            zIndex = administrativeUnit.zIndex()
        )
    }
}