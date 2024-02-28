package io.schiar.fridgnet.model.repository

import io.schiar.fridgnet.Log
import io.schiar.fridgnet.model.Address
import io.schiar.fridgnet.model.AddressLocationImages
import io.schiar.fridgnet.model.AdministrativeUnit
import io.schiar.fridgnet.model.repository.address.AddressRepository
import io.schiar.fridgnet.model.repository.image.ImageRepository
import io.schiar.fridgnet.model.repository.listeners.OnAddressReadyListener
import io.schiar.fridgnet.model.repository.listeners.OnLocationReadyListener
import io.schiar.fridgnet.model.repository.listeners.OnNewImageAddedListener
import io.schiar.fridgnet.model.repository.location.LocationRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class HomeRepository(
    private val addressRepository: AddressRepository,
    private val locationRepository: LocationRepository,
    private val imageRepository: ImageRepository,
    private val onAddressReadyListener: OnAddressReadyListener,
    private val onNewImageAddedListener: OnNewImageAddedListener
): OnLocationReadyListener {
    private var onLocationReadyCallback: suspend () -> Unit = {}

    fun subscribeForNewAddressAdded(callback: suspend () -> Unit) {
        addressRepository.subscribeForNewAddressAdded { address ->
            onNewAddressAdded(address = address, callback = callback)
        }
    }

    private suspend fun onNewAddressAdded(address: Address, callback: suspend () -> Unit) {
        onAddressReadyListener.onAddressReady(address)
        callback()
    }

    fun subscribeForLocationsReady(callback: suspend () -> Unit) {
        onLocationReadyCallback = callback
    }

    suspend fun selectImagesFrom(addressName: String) {
        Log.d("Select Image Feature", "Searching Images for $addressName")
        val (address, coordinates) = addressRepository.coordinatesFromAddressName(
            addressName, ::onNewImageArrived
        )
        imageRepository.currentImages = Pair(address ?: return, imageRepository.imagesFromCoordinates(coordinates))
    }

    private suspend fun onNewImageArrived() {
        val (address, coordinates) = addressRepository.currentCoordinates()
        imageRepository.currentImages = Pair(address ?: return, imageRepository.imagesFromCoordinates(coordinates))
        onNewImageAddedListener.onNewImageAdded()
    }

    suspend fun locationImages(): List<AddressLocationImages> {
        Log.d("Add Image Feature", "Updating Location Images")
        return addressRepository.currentAddressCoordinates()
            .map { (address, coordinates) ->
                AddressLocationImages(
                    address = address,
                    location = withContext(Dispatchers.IO) { locationRepository.locationAddress[address] },
                    initialCoordinate = coordinates.first()
                )
            }
    }

    fun changeCurrent(administrativeUnit: AdministrativeUnit) {
        addressRepository.currentAdministrativeUnit = administrativeUnit
    }

    suspend fun removeAllImages() {
        imageRepository.removeAllImages()
    }

    override suspend fun onLocationReady() {
        onLocationReadyCallback()
    }
}