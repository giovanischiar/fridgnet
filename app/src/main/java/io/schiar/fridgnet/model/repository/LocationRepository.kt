package io.schiar.fridgnet.model.repository

import android.util.Log
import io.schiar.fridgnet.model.*
import io.schiar.fridgnet.model.repository.nominatim.GeoJson
import io.schiar.fridgnet.model.repository.nominatim.GeoJsonAttributes
import io.schiar.fridgnet.model.repository.nominatim.PolygonSearcher
import io.schiar.fridgnet.viewmodel.util.*
import kotlinx.coroutines.*
import kotlinx.coroutines.sync.Mutex

class LocationRepository() {
    private var fetchingPlaces: Set<String> = emptySet()
    private var mutex: Mutex = Mutex()
    private var onRegionLocationReady: (regionLocation: Map<Region, Location>) -> Unit = {}

    fun subscribeOnRegionLocationReady(onRegionLocationReady: (Map<Region, Location>) -> Unit) {
        this.onRegionLocationReady = onRegionLocationReady
    }

    suspend fun fetchLocations(address: Address) = coroutineScope {
        val handler = CoroutineExceptionHandler { _, exception ->
            Log.d("Network", "Caught $exception")
        }
        launch(handler) { addCityLocation(address = address) }
        launch(handler) { addCountyLocation(address = address) }
        launch(handler) { addStateLocation(address = address) }
        launch(handler) { addCountryLocation(address = address) }
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

    private suspend fun addCityLocation(address: Address) {
        val newAddress = if (address.locality == null) {
            extractAddress(address = address)
        } else {
            address
        }
        val addressStr = newAddress.name()
        if (fetchingPlaces.contains(addressStr)) return
        fetchingPlaces = fetchingPlaces + addressStr
        val location = fetchLocation(
            address = newAddress,
            administrativeUnit = AdministrativeUnit.CITY
        ) ?: return
        addRegionsWithin(location = location)
    }

    private suspend fun addCountryLocation(address: Address) {
        address.countryName ?: return
        val countryAddressName = address.addressAccordingTo(
            administrativeUnit = AdministrativeUnit.COUNTRY
        ).name()
        if (fetchingPlaces.contains(countryAddressName)) return
        fetchingPlaces = fetchingPlaces + countryAddressName
        val location = fetchLocation(
            address = address,
            administrativeUnit = AdministrativeUnit.COUNTRY
        ) ?: return
        addRegionsWithin(location = location)
    }

    private suspend fun addStateLocation(address: Address) {
        address.adminArea ?: return
        address.countryName ?: return
        val stateAddressName = address.addressAccordingTo(
            administrativeUnit = AdministrativeUnit.STATE
        ).name()
        if (fetchingPlaces.contains(stateAddressName)) return
        fetchingPlaces = fetchingPlaces + stateAddressName
        val location = fetchLocation(
            address = address,
            administrativeUnit = AdministrativeUnit.STATE
        ) ?: return
        addRegionsWithin(location = location)
    }

     private suspend fun addCountyLocation(address: Address) {
        address.adminArea ?: return
        address.subAdminArea ?: return
        val countyAddressName = address.addressAccordingTo(
             administrativeUnit = AdministrativeUnit.COUNTY
        ).name()

        if (fetchingPlaces.contains(countyAddressName)) return
        fetchingPlaces = fetchingPlaces + countyAddressName
         val location = fetchLocation(
            address = address,
            administrativeUnit = AdministrativeUnit.COUNTY
         ) ?: return
         addRegionsWithin(location = location)
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
        val results = withContext(Dispatchers.Default) {
            when (administrativeUnit) {
                AdministrativeUnit.CITY -> {
                    polygonSearcher.searchCity(city = city, state = state, country = country)
                }

                AdministrativeUnit.COUNTY -> {
                    polygonSearcher.searchCounty(county = county, state = state, country = country)
                }

                AdministrativeUnit.STATE -> {
                    polygonSearcher.searchState(state = state, country = country)
                }

                AdministrativeUnit.COUNTRY -> {
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

    private fun addRegionsWithin(location: Location) {
        for (region in location.regions) {
            onRegionLocationReady(mapOf(region to location))
        }
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
                val regions = pointDoubleList.toPolygonCoordinates().map {
                    val polygon = Polygon(coordinates = it)
                    Region(
                        polygon = polygon,
                        holes = emptyList(),
                        boundingBox = polygon.findBoundingBox(),
                        zIndex = administrativeUnit.zIndex()
                    )
                }
                regions
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
            administrativeUnit = administrativeUnit,
            regions = regions,
            boundingBox = boundingBox,
            zIndex = administrativeUnit.zIndex()
        )
    }
}