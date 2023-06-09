package io.schiar.fridgnet.model.repository

import io.schiar.fridgnet.Log
import io.schiar.fridgnet.model.*
import io.schiar.fridgnet.model.repository.address.AddressRepository
import io.schiar.fridgnet.model.repository.image.ImageRepository
import io.schiar.fridgnet.model.repository.location.LocationRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext
import java.util.Collections.synchronizedMap as syncMapOf

class MainRepository(
    private val locationRepository: LocationRepository,
    private val addressRepository: AddressRepository,
    private val imageRepository: ImageRepository,
) : AppRepository, HomeRepository, MapRepository, PhotosRepository, PolygonsRepository {
    private var onAddressOnImageAdded: () -> Unit = {}
    private var onLocationReady: () -> Unit = {}
    private var onImageAdded: () -> Unit = {}
    private var currentAdministrativeUnit = AdministrativeUnit.CITY
    private var currentImages: Pair<Address, List<Image>>? = null
    private val locationAddress: MutableMap<Address, Location> = syncMapOf(mutableMapOf())
    private val nameAddress: MutableMap<String, Address> = syncMapOf(mutableMapOf())
    private val cityImages: MutableMap<Address, List<Image>> = syncMapOf(mutableMapOf())
    private val countyImages: MutableMap<Address, List<Image>> = syncMapOf(mutableMapOf())
    private val stateImages: MutableMap<Address, List<Image>> = syncMapOf(mutableMapOf())
    private val countryImages: MutableMap<Address, List<Image>> = syncMapOf(mutableMapOf())

    // AppViewModel
    override suspend fun loadDatabase(onDatabaseLoaded: () -> Unit) = coroutineScope {
        withContext(Dispatchers.IO) { locationRepository.setup() }
        onDatabaseLoaded()
    }

    override suspend fun addURIs(uris: List<String>) {
        imageRepository.addImages(uris = uris, onReady = ::onImageAdded)
    }

    // HomeViewModel
    override fun subscribeForAddressImageAdded(callback: () -> Unit) {
        onAddressOnImageAdded = callback
    }

    override fun subscribeForLocationsReady(callback: () -> Unit) {
        onLocationReady = callback
    }

    override fun selectImagesFrom(addressName: String) {
        val address = nameAddress[addressName] ?: return
        val images =  when (currentAdministrativeUnit) {
            AdministrativeUnit.CITY -> cityImages[address]
            AdministrativeUnit.COUNTY -> countyImages[address]
            AdministrativeUnit.STATE -> stateImages[address]
            AdministrativeUnit.COUNTRY -> stateImages[address]
        } ?: return
        currentImages = address to images
    }

    override fun locationImages(): List<AddressLocationImages> {
        Log.d("Add Image Feature", "Updating Location Images")
        return when (currentAdministrativeUnit) {
            AdministrativeUnit.CITY -> cityImages
            AdministrativeUnit.COUNTY -> countyImages
            AdministrativeUnit.STATE -> stateImages
            AdministrativeUnit.COUNTRY -> countryImages
        }.map { (address, images) ->
            AddressLocationImages(
                address = address,
                location = locationAddress[address],
                initialCoordinate = images[0].coordinate
            )
        }
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
    override fun subscribeForNewImages(callback: () -> Unit) {
        onImageAdded = callback
    }

    override fun currentImages(): Pair<Address, List<Image>>? {
        return currentImages
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
            addLocationToAddress(location = location)
            onLocationReady()
        }
    }

    private fun addLocationToAddress(location: Location) {
        Log.d("Add Image Feature", "add ${location.address} to $location")
        locationAddress[location.address] = location
    }

    private fun addAddressToImage(address: Address, image: Image) {
        Log.d("Add Image Feature", "add $image to $address")
        val images = addImagesToEachAddress(address = address, image = image)
        if (images.size == 1 && address.administrativeUnit == currentAdministrativeUnit) {
            nameAddress[address.name()] = address
            onAddressOnImageAdded()
        }

        if (currentImages?.first == address) {
            selectImagesFrom(address.name())
            onImageAdded()
        }
    }

    private fun addImagesToEachAddress(address: Address, image: Image): List<Image> {
        return when (address.administrativeUnit) {
            AdministrativeUnit.CITY -> {
                val images = cityImages.getOrDefault(address, listOf()) + image
                cityImages[address] = images
                images
            }
            AdministrativeUnit.COUNTY -> {
                val images = countyImages.getOrDefault(address, listOf()) + image
                countyImages[address] = images
                images
            }
            AdministrativeUnit.STATE -> {
                val images = stateImages.getOrDefault(address, listOf()) + image
                stateImages.getOrDefault(address, listOf()) + image
                images
            }
            AdministrativeUnit.COUNTRY -> {
                val images = countryImages.getOrDefault(address, listOf()) + image
                countryImages.getOrDefault(address, listOf()) + image
                images
            }
        }
    }
}