package io.schiar.fridgnet.model.repository

import io.schiar.fridgnet.Log
import io.schiar.fridgnet.model.*
import io.schiar.fridgnet.model.repository.address.AddressRepository
import io.schiar.fridgnet.model.repository.image.ImageRepository
import io.schiar.fridgnet.model.repository.location.LocationRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext
import kotlin.streams.toList
import java.util.Collections.synchronizedMap as syncMapOf

class MainRepository(
    private val locationRepository: LocationRepository,
    private val addressRepository: AddressRepository,
    private val imageRepository: ImageRepository,
) : AppRepository, HomeRepository, MapRepository, PhotosRepository, PolygonsRepository {
    private var onLocationReady: suspend () -> Unit = {}
    private var onImageAdded: suspend () -> Unit = {}
    private var currentImages: Pair<Address, Set<Image>>? = null
    private val locationAddress: MutableMap<Address, Location> = syncMapOf(mutableMapOf())

    // AppViewModel
    override suspend fun loadDatabase(onDatabaseLoaded: () -> Unit) = coroutineScope {
        withContext(Dispatchers.IO) {
            imageRepository.setup()
            addressRepository.setup()
            locationRepository.setup()
        }
        onDatabaseLoaded()
    }

    override suspend fun addURIs(uris: List<String>) {
        imageRepository.addImages(uris = uris, onReady = ::onImageAdded)
    }

    // HomeViewModel
    override fun subscribeForNewAddressAdded(callback: suspend () -> Unit) {
        addressRepository.subscribeForNewAddressAdded { address ->
            onNewAddressAdded(address = address, callback = callback)
        }
    }

    private suspend fun onNewAddressAdded(address: Address, callback: suspend () -> Unit) {
        onAddressReady(address)
        callback()
    }

    override fun subscribeForLocationsReady(callback: suspend () -> Unit) {
        onLocationReady = callback
    }

    override suspend fun selectImagesFrom(addressName: String) {
        Log.d("Select Image Feature", "Searching Images for $addressName")
        val (address, coordinates) = addressRepository.coordinatesFromAddressName(
            addressName, ::onNewImageArrived
        )
        currentImages = Pair(address ?: return, imageRepository.imagesFromCoordinates(coordinates))
    }

    private suspend fun onNewImageArrived() {
        val (address, coordinates) = addressRepository.currentCoordinates()
        currentImages = Pair(address ?: return, imageRepository.imagesFromCoordinates(coordinates))
        onImageAdded()
    }

    override suspend fun locationImages(): List<AddressLocationImages> {
        Log.d("Add Image Feature", "Updating Location Images")
        return addressRepository.currentAddressCoordinates()
            .map { (address, coordinates) ->
            AddressLocationImages(
                address = address,
                location = withContext(Dispatchers.IO) { locationAddress[address] },
                initialCoordinate = coordinates.first()
            )
        }
    }

    override fun changeCurrent(administrativeUnit: AdministrativeUnit) {
        addressRepository.currentAdministrativeUnit = administrativeUnit
    }

    override suspend fun removeAllImages() {
        imageRepository.removeAllImages()
    }

    // MapViewModel
    override fun selectNewLocationFrom(region: Region) {
        locationRepository.selectNewLocationFrom(region = region)
    }

    override fun visibleImages(boundingBox: BoundingBox): List<Image> {
        return imageRepository.imagesThatIntersect(boundingBox = boundingBox)
    }

    override fun visibleRegions(boundingBox: BoundingBox): List<Region> {
        return locationRepository.regionsThatIntersect(boundingBox = boundingBox)
    }

    override fun boundingBoxCities(): BoundingBox? {
        return locationRepository.allCitiesBoundingBox
    }

    // PolygonsViewModel
    override fun currentLocation(): Location? {
        return locationRepository.currentLocation
    }

    override suspend fun switchRegion(region: Region, onCurrentLocationChanged: () -> Unit) {
        withContext(Dispatchers.IO) { locationRepository.switchRegion(region = region) }
        onCurrentLocationChanged()
    }

    override suspend fun switchAll(onCurrentLocationChanged: () -> Unit) {
        withContext(Dispatchers.IO) { locationRepository.switchAll() }
        onCurrentLocationChanged()
    }

    // PhotosViewModel
    override fun subscribeForNewImages(callback: suspend () -> Unit) {
        onImageAdded = callback
    }

    override fun currentImages(): Pair<Address, List<Image>>? {
        val first = currentImages?.first ?: return null
        val second = currentImages?.second ?: return null
        return (first to second.toList())
    }

    override fun selectedLocation(): Location? {
        return locationAddress[currentImages?.first]
    }

    override fun selectedBoundingBox(): BoundingBox? {
        val location = locationAddress[currentImages?.first] ?: return null
        var boundingBox = location.boundingBox
        for (image in (currentImages ?: return null).second.stream()) {
            if (!boundingBox.contains(image.coordinate)) {
                boundingBox += image.coordinate
            }
        }
        return boundingBox
    }

    override fun selectedImagesBoundingBox(): BoundingBox? {
        val coordinates = (currentImages ?: return null).second.stream().map {
            it.coordinate
        }.toList()
        return Polygon(coordinates = coordinates).findBoundingBox()
    }

    private suspend fun onImageAdded(image: Image) {
        val coordinate = image.coordinate
        Log.d(
            "Add Image Feature",
            "Image added! getting the address of the image located at $coordinate"
        )
        val address = withContext(Dispatchers.IO) {
            addressRepository.fetchAddressBy(coordinate = coordinate)
        } ?: return

        address.allAddresses().forEach { subAddress -> onAddressReady(address = subAddress) }
    }

    private suspend fun onAddressReady(address: Address) {
        locationRepository.loadRegions(address = address) { location ->
            addLocationToAddress(location = location)
            onLocationReady()
        }
    }

    private fun addLocationToAddress(location: Location) {
        Log.d("Add Image Feature", "add ${location.address} to $location")
        locationAddress[location.address] = location
    }
}