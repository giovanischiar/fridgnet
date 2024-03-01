package io.schiar.fridgnet.model.repository

import io.schiar.fridgnet.Log
import io.schiar.fridgnet.model.repository.address.AddressRepository
import io.schiar.fridgnet.model.repository.image.ImageRepository
import io.schiar.fridgnet.model.repository.listeners.OnImageAddedListener
import io.schiar.fridgnet.model.repository.location.LocationRepository
import kotlin.system.measureTimeMillis

class AppRepository(
    private val locationRepository: LocationRepository,
    private val addressRepository: AddressRepository,
    private val imageRepository: ImageRepository,
    private val onImageAddedListener: OnImageAddedListener
) {
    suspend fun loadDatabase(onDatabaseLoaded: () -> Unit) {
        Log.d(tag = "Load Database",
            msg = "database Loaded! time elapsed: ${
                measureTimeMillis { 
                    imageRepository.setup()
                    addressRepository.setup()
                    locationRepository.setup() 
                }
            }"
        )
        onDatabaseLoaded()
    }

    suspend fun addURIs(uris: List<String>) {
        imageRepository.addImages(uris = uris, onReady = onImageAddedListener::onImageAdded)
    }
}