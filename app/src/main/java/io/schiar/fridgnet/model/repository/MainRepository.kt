package io.schiar.fridgnet.model.repository

import io.schiar.fridgnet.Log
import io.schiar.fridgnet.model.*
import io.schiar.fridgnet.model.repository.address.AddressRepository
import io.schiar.fridgnet.model.repository.image.ImageRepository
import io.schiar.fridgnet.model.repository.location.LocationRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext
import java.util.*

class MainRepository(
    private val locationRepository: LocationRepository,
    private val addressRepository: AddressRepository,
    private val imageRepository: ImageRepository
) : Repository {

    private var onAddressOnImageAdded: (address: String, images: List<Image>) -> Unit = { _, _ ->
    }
    private var onLocationReady: (location: Location) -> Unit = {}
    private val addressImages: MutableMap<String, List<Image>> = Collections.synchronizedMap(
        mutableMapOf()
    )

    override suspend fun loadDatabase(onDatabaseLoaded: () -> Unit) = coroutineScope {
        withContext(Dispatchers.IO) { locationRepository.setup() }
        onDatabaseLoaded()
    }

    override suspend fun addURIs(uris: List<String>) {
        imageRepository.addImages(uris = uris, onReady = ::onImageAdded)
    }

    override fun subscribeForAddressImageAdded(callback: (address: String, images: List<Image>) -> Unit) {
        onAddressOnImageAdded = callback
    }

    override fun subscribeForLocationsReady(callback: (location: Location) -> Unit) {
        onLocationReady = callback
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

    override fun selectNewLocationFrom(region: Region): Location? {
        return locationRepository.selectNewLocationFrom(region = region)
    }

    override fun selectImagesFrom(addressName: String): List<Image>? {
        return addressImages[addressName]
    }

    override suspend fun switchRegion(
        region: Region,
        onCurrentLocationChanged: (location: Location?) -> Unit)
    {
        withContext(Dispatchers.IO) { locationRepository.switchRegion(region = region) }
        onCurrentLocationChanged(locationRepository.currentLocation)
    }

    override suspend fun switchAll(onCurrentLocationChanged: (location: Location?) -> Unit) {
        withContext(Dispatchers.IO) { locationRepository.switchAll() }
        onCurrentLocationChanged(locationRepository.currentLocation)
    }

    private suspend fun onImageAdded(image: Image) {
        val coordinate = image.coordinate
        Log.d(
            "Add Image Feature",
            "Image added! getting the address of the image located at $coordinate"
        )
        addressRepository.getAddressFrom(coordinate = coordinate) { address ->
            onAddressReady(image = image, address = address)
        }
    }

    private suspend fun onAddressReady(image: Image, address: Address) {
        addAddressToImage(image = image, address = address)
        locationRepository.loadRegions(address = address) { location ->
            onLocationReady(location)
        }
    }

    private fun addAddressToImage(image: Image, address: Address) {
        val images = addressImages.getOrDefault(address.name(), listOf()) + image
        addressImages[address.name()] = images
        onAddressOnImageAdded(address.name(), images)
    }
}