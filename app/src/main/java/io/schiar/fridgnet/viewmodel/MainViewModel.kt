package io.schiar.fridgnet.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.schiar.fridgnet.model.*
import io.schiar.fridgnet.model.nominatim.GeoJson
import io.schiar.fridgnet.model.nominatim.GeoJsonAttributes
import io.schiar.fridgnet.model.nominatim.PolygonSearcher
import io.schiar.fridgnet.view.viewdata.BoundingBoxViewData
import io.schiar.fridgnet.view.viewdata.ImageViewData
import io.schiar.fridgnet.view.viewdata.LocationViewData
import io.schiar.fridgnet.view.viewdata.RegionViewData
import io.schiar.fridgnet.viewmodel.util.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.sync.Mutex

class MainViewModel: ViewModel() {
    private var _images: Map<String, Image> = emptyMap()
    private var _addressImages: Map<String, List<Image>> = emptyMap()
    private var _locationAddress: Map<String, Location> = emptyMap()
    private var _location: Location? = null
    private var _regionLocation: Map<Region, Location> = emptyMap()
    private var _regions: List<Region> = listOf()
    private var mutex: Mutex = Mutex()

    private var fetchingPlaces: Set<String> = emptySet()

    private var countries: Map<String, Location> = emptyMap()
    private var states: Map<String, Map<String, Location>> = emptyMap()
    private var counties: Map<String, Map<String, Location>> = emptyMap()
    private var cities: Map<String, Map<String, Location>> = emptyMap()

    private val _visibleImages = MutableStateFlow(value = _images.toImageViewData())
    val visibleImages: StateFlow<List<ImageViewData>> = _visibleImages.asStateFlow()

    private val _selectedImages = MutableStateFlow<List<ImageViewData>>(value = emptyList())
    val selectedImages: StateFlow<List<ImageViewData>> = _selectedImages.asStateFlow()

    private val _imageWithLocations = MutableStateFlow(
        value = _addressImages.toStringImageViewDataList()
    )
    val imagesWithLocation: StateFlow<Map<String, List<ImageViewData>>> =
        _imageWithLocations.asStateFlow()

    private val _allLocationAddress = MutableStateFlow(
        value = _locationAddress.toStringLocationViewData()
    )
    val allLocationAddress: StateFlow<Map<String, LocationViewData>> =
        _allLocationAddress.asStateFlow()

    private val _currentLocation = MutableStateFlow(_location?.toLocationViewData())
    val currentLocation: StateFlow<LocationViewData?> = _currentLocation.asStateFlow()

    private val _visibleRegions: MutableStateFlow<List<RegionViewData>>
        = MutableStateFlow(emptyList())

    val visibleRegions: StateFlow<List<RegionViewData>> = _visibleRegions.asStateFlow()

    fun addImage(uri: String, date: Long, latitude: Double, longitude: Double) {
        val newCoordinate = Coordinate(latitude = latitude, longitude = longitude)
        val newImage = Image(uri = uri, date = date, coordinate = newCoordinate)
        _images = _images + (uri to newImage)
    }

    fun addAddressToImage(uri: String, systemAddress: android.location.Address) {
        val address = systemAddress.toAddress()
        Log.d("searchForLocation", "searchForLocation ${address.name()}")
        val handler = CoroutineExceptionHandler { _, exception ->
            Log.d("Network", "Caught $exception")
        }

        viewModelScope.launch(handler) {
            launch { addCityLocation(address = address, uri = uri) }
            launch { addCountyLocation(address = address) }
            launch { addStateLocation(address = address) }
            launch { addCountryLocation(address = address) }
        }
    }

    fun selectImages(address: String) {
        if (_addressImages.containsKey(address)) {
            _selectedImages.update { _addressImages[address]!!.toImageViewDataList() }
        }
    }

    fun visibleAreaChanged(boundingBoxViewData: BoundingBoxViewData) {
        val boundingBox = boundingBoxViewData.toBoundingBox()
        val visibleImages = _images.values.filter { image ->
            boundingBox.contains(coordinate = image.coordinate)
        }
        _visibleImages.update { visibleImages.toImageViewDataList() }
        _visibleRegions.update {
            _regions.filter {
                region -> boundingBox.contains(other = region.boundingBox)
            }.toRegionViewDataList()
        }
    }

    fun selectRegion(regionViewData: RegionViewData) {
        val region = regionViewData.toRegion()
        if (_regionLocation.containsKey(region)) {
            _location = _regionLocation[region]
            _currentLocation.update { _location?.toLocationViewData() }
        }
    }

    private fun addRegionsWithin(location: Location) {
        _regions = _regions + location.regions
        for (region in location.regions) {
            _regionLocation = _regionLocation + (region to location)
        }
    }

    private suspend fun addCountryLocation(address: Address) {
        val country = address.countryName ?: return
        if (countries.containsKey(country)) return
        if (fetchingPlaces.contains(country)) return
        fetchingPlaces = fetchingPlaces + country
        val location = fetchLocation(address = address, type = Regions.COUNTRY) ?: return
        addRegionsWithin(location = location)
        countries = countries + (country to location)
    }

    private suspend fun addStateLocation(address: Address) {
        val state = address.adminArea ?: return
        val country = address.countryName ?: return
        if (states.containsKey(country) && states[country]!!.containsKey(state)) return
        if (fetchingPlaces.contains("$state, $country")) return
        fetchingPlaces = fetchingPlaces + "$state, $country"
        val location = fetchLocation(address = address, type = Regions.STATE) ?: return
        addRegionsWithin(location = location)
        val mutableStates = states.toMutableMap()
        val mutableStatesLocation = (mutableStates[country] ?: mutableMapOf()).toMutableMap()
        mutableStatesLocation[state] = location
        mutableStates[country] = mutableStatesLocation
        states = mutableStates.toMap()
    }

    private suspend fun addCountyLocation(address: Address) {
        val state = address.adminArea ?: return
        val county = address.subAdminArea ?: return
        if (counties.containsKey(state) && counties[state]!!.containsKey(county)) return
        if (fetchingPlaces.contains("$county, $state")) return
        fetchingPlaces = fetchingPlaces + "$county, $state"
        val location = fetchLocation(address = address, type = Regions.COUNTY) ?: return
        addRegionsWithin(location = location)
        val mutableCounties = counties.toMutableMap()
        val mutableCountiesLocation = (mutableCounties[state] ?: mapOf()).toMutableMap()
        mutableCountiesLocation[county] = location
        mutableCounties[state] = mutableCountiesLocation
        counties = mutableCounties.toMap()
    }

    private fun extractAddress(address: Address): Address {
        val name = address.name()
        val add = name.split(", ")
        if (add.size < 2) return address
        val state = add[1]
        val city = add[0]
        return Address(locality = city, subAdminArea = null, adminArea = state, countryName = address.countryName)
    }

    private suspend fun addCityLocation(address: Address, uri: String) {
        var city = address.locality ?: ""
        var state = address.adminArea ?: ""
        if (address.locality == null) {
            val newAddress = extractAddress(address = address)
            city = newAddress.locality  ?: ""
            state = newAddress.adminArea ?: ""
        }
        val addressStr = "$city, $state"

        if (_images.containsKey(uri) && !_addressImages.containsKey(addressStr)) {
            _addressImages = _addressImages + if (_addressImages.containsKey(addressStr)) {
                (addressStr to (_addressImages[addressStr]!! + _images[uri]!!))
            } else {
                (addressStr to listOf(_images[uri]!!))
            }
            _imageWithLocations.update { _addressImages.toStringImageViewDataList() }
        }

        Log.d("API Result", "city: $city, state: $state")
        if (cities.containsKey(state) && cities[state]!!.containsKey(city)) return
        if (fetchingPlaces.contains(addressStr)) return
        fetchingPlaces = fetchingPlaces + addressStr
        val newAddress = Address(locality = city, subAdminArea = "", adminArea = state, countryName = "")
        val location = fetchLocation(address = newAddress, type = Regions.CITY) ?: return
        addRegionsWithin(location = location)
        val mutableCities = cities.toMutableMap()
        val mutableCitiesLocation = (mutableCities[state] ?: mapOf()).toMutableMap()
        mutableCitiesLocation[city] = location
        mutableCities[state] = mutableCitiesLocation
        cities = mutableCities.toMap()
        _locationAddress = _locationAddress + (addressStr to location)
        _allLocationAddress.update { _locationAddress.toStringLocationViewData() }
    }
    private suspend fun fetchLocation(address: Address, type: Regions): Location? {
        val polygonSearcher = PolygonSearcher()
        val city = address.locality ?: ""
        val county = address.subAdminArea ?: ""
        val state = address.adminArea ?: ""
        val country = address.countryName ?: ""
        mutex.lock()
        val results = withContext(Dispatchers.Default) {
            when (type) {
                Regions.CITY -> {
                    polygonSearcher.searchCity(city = city, state = state, country = country)
                }

                Regions.COUNTY -> {
                    polygonSearcher.searchCounty(county = county, state = state, country = country)
                }

                Regions.STATE -> {
                    polygonSearcher.searchState(state = state, country = country)
                }

                Regions.COUNTRY -> {
                    polygonSearcher.searchCountry(country = country)
                }
            }
        }
        delay(1000) //Requests to Nominatim API should be limit to one per second
        mutex.unlock()
        val bodyList = results.body() ?: return null
        if (bodyList.isEmpty()) return null
        val body = bodyList[0]
        Log.d("API Result", "type: $type, address: ${address.name()} body.geojson: ${body.geojson}")
        val geoJson = body.geojson
        val boundingBox = body.boundingbox.toBoundingBox()
        return bodyToLocation(geoJson = geoJson, boundingBox = boundingBox)
    }

    private fun bodyToLocation(geoJson: GeoJson<GeoJsonAttributes>, boundingBox: BoundingBox): Location? {
        return when (geoJson.type) {
            "Point" -> {
                val pointDoubleList = geoJson.coordinates as List<Double>
                val polygon = Polygon(coordinates = listOf(pointDoubleList.toCoordinate()))
                val region = Region(
                    polygon = polygon,
                    holes = emptyList(),
                    boundingBox = polygon.findBoundingBox()
                )
                Location(regions = listOf(region), boundingBox = boundingBox)
            }
            "LineString" -> {
                val pointDoubleList = geoJson.coordinates as List<List<Double>>
                val polygon = Polygon(coordinates = pointDoubleList.toLineStringCoordinates())
                val region = Region(
                    polygon = polygon,
                    holes = emptyList(),
                    boundingBox = polygon.findBoundingBox()
                )
                Location(regions = listOf(region), boundingBox = boundingBox)
            }

            "Polygon" -> {
                val pointDoubleList = geoJson.coordinates as List<List<List<Double>>>
                val regions = pointDoubleList.toPolygonCoordinates().map {
                    val polygon = Polygon(coordinates = it)
                    Region(
                        polygon = polygon,
                        holes = emptyList(),
                        boundingBox = polygon.findBoundingBox()
                    )
                }
                Location(regions = regions, boundingBox = boundingBox)
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
                        boundingBox = polygon.findBoundingBox()
                    )
                }
                Location(regions = regions, boundingBox = boundingBox)
            }

            else -> return null
        }
    }
}