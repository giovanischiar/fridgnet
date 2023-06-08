package io.schiar.fridgnet.model.repository

import io.schiar.fridgnet.model.*

interface Repository {
    suspend fun loadDatabase(onDatabaseLoaded: () -> Unit)

    suspend fun addURIs(uris: List<String>)
    fun subscribeForAddressImageAdded(callback: () -> Unit)
    fun subscribeForLocationsReady(callback: () -> Unit)
    fun visibleImages(boundingBox: BoundingBox): List<Image>
    fun visibleRegions(boundingBox: BoundingBox): List<Region>
    fun boundingBoxCities(): BoundingBox?
    fun selectNewLocationFrom(region: Region): Location?
    fun selectImagesFrom(addressName: String): List<Image>?
    fun locationImages(): List<AddressLocationImages>

    suspend fun switchRegion(
        region: Region,
        onCurrentLocationChanged: (location: Location?) -> Unit
    )

    suspend fun switchAll(onCurrentLocationChanged: (location: Location?) -> Unit)
}