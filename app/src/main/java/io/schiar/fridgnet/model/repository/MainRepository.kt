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
    private var onAddressOnImageAdded: () -> Unit = {}
    private var onLocationReady: () -> Unit = {}
    private val locationAddress: MutableMap<Address, Location> =
        Collections.synchronizedMap(mutableMapOf())

    private val nameAddress: MutableMap<String, Address> = Collections.synchronizedMap(
        mutableMapOf()
    )

    private var currentAdministrativeUnit = AdministrativeUnit.CITY

    private var cityImages: MutableMap<Address, List<Image>> = Collections.synchronizedMap(
        mutableMapOf()
    )

    private var countyImages: MutableMap<Address, List<Image>> = Collections.synchronizedMap(
        mutableMapOf()
    )

    private var stateImages: MutableMap<Address, List<Image>> = Collections.synchronizedMap(
        mutableMapOf()
    )

    private var countryImages: MutableMap<Address, List<Image>> = Collections.synchronizedMap(
        mutableMapOf()
    )

    override suspend fun loadDatabase(onDatabaseLoaded: () -> Unit) = coroutineScope {
        withContext(Dispatchers.IO) { locationRepository.setup() }
        onDatabaseLoaded()
    }

    override suspend fun addURIs(uris: List<String>) {
        imageRepository.addImages(uris = uris, onReady = ::onImageAdded)
    }

    override fun subscribeForAddressImageAdded(callback: () -> Unit) {
        onAddressOnImageAdded = callback
    }

    override fun subscribeForLocationsReady(callback: () -> Unit) {
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
        return when (currentAdministrativeUnit) {
            AdministrativeUnit.CITY -> cityImages[nameAddress[addressName]]
            AdministrativeUnit.COUNTY -> countyImages[nameAddress[addressName]]
            AdministrativeUnit.STATE -> stateImages[nameAddress[addressName]]
            AdministrativeUnit.COUNTRY -> stateImages[nameAddress[addressName]]
        }
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