package io.schiar.fridgnet.model.repository

import io.schiar.fridgnet.Log
import io.schiar.fridgnet.model.Address
import io.schiar.fridgnet.model.BoundingBox
import io.schiar.fridgnet.model.Image
import io.schiar.fridgnet.model.Location
import io.schiar.fridgnet.model.Polygon
import io.schiar.fridgnet.model.repository.address.AddressRepository
import io.schiar.fridgnet.model.repository.image.ImageRepository
import io.schiar.fridgnet.model.repository.listeners.OnAddressReadyListener
import io.schiar.fridgnet.model.repository.listeners.OnImageAddedListener
import io.schiar.fridgnet.model.repository.listeners.OnLocationReadyListener
import io.schiar.fridgnet.model.repository.listeners.OnNewImageAddedListener
import io.schiar.fridgnet.model.repository.location.LocationRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class PhotosRepository(
    private val imageRepository: ImageRepository,
    private val locationRepository: LocationRepository,
    private val addressRepository: AddressRepository,
): OnImageAddedListener, OnAddressReadyListener, OnNewImageAddedListener {
    private var onNewImageAddedCallback: suspend () -> Unit = {}
    var onLocationReadyListener: OnLocationReadyListener? = null

    fun subscribeForNewImages(callback: suspend () -> Unit) {
        onNewImageAddedCallback = callback
    }

    fun currentImages(): Pair<Address, List<Image>>? {
        val first = imageRepository.currentImages?.first ?: return null
        val second = imageRepository.currentImages?.second ?: return null
        return (first to second.toList())
    }

    fun selectedLocation(): Location? {
        return locationRepository.locationAddress[imageRepository.currentImages?.first]
    }

    fun selectedBoundingBox(): BoundingBox? {
        val location = locationRepository.locationAddress[imageRepository.currentImages?.first] ?: return null
        var boundingBox = location.boundingBox
        for (image in (imageRepository.currentImages ?: return null).second.stream()) {
            if (!boundingBox.contains(image.coordinate)) {
                boundingBox += image.coordinate
            }
        }
        return boundingBox
    }

    fun selectedImagesBoundingBox(): BoundingBox? {
        val coordinates = (imageRepository.currentImages ?: return null).second.stream().map {
            it.coordinate
        }.toList()
        return Polygon(coordinates = coordinates).findBoundingBox()
    }

    override suspend fun onImageAdded(image: Image) {
        val coordinate = image.coordinate
        Log.d(
            "Add Image Feature",
            "Image added! getting the address of the image located at $coordinate"
        )
        val address = addressRepository.fetchAddressBy(coordinate = coordinate) ?: return

        address.allAddresses().forEach { subAddress -> onAddressReady(address = subAddress) }
    }

    override suspend fun onAddressReady(address: Address) {
        locationRepository.loadRegions(address = address) { location ->
            addLocationToAddress(location = location)
            onLocationReadyListener?.onLocationReady()
        }
    }

    private fun addLocationToAddress(location: Location) {
        Log.d("Add Image Feature", "add ${location.address} to $location")
        locationRepository.locationAddress[location.address] = location
    }

    override suspend fun onNewImageAdded() { onNewImageAddedCallback() }
}