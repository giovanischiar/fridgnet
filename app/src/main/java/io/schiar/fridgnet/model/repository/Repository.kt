package io.schiar.fridgnet.model.repository

import io.schiar.fridgnet.model.*

interface Repository {
    suspend fun loadDatabase(onDatabaseLoaded: () -> Unit)
    suspend fun addURIs(uris: List<String>)
    suspend fun switchRegion(region: Region, onCurrentLocationChanged: () -> Unit)
    suspend fun switchAll(onCurrentLocationChanged: () -> Unit)

    fun subscribeForAddressImageAdded(callback: () -> Unit)
    fun subscribeForLocationsReady(callback: () -> Unit)
    fun subscribeForNewImages(callback: () -> Unit)
    fun visibleImages(boundingBox: BoundingBox): List<Image>
    fun visibleRegions(boundingBox: BoundingBox): List<Region>
    fun boundingBoxCities(): BoundingBox?
    fun selectNewLocationFrom(region: Region)
    fun currentLocation(): Location?
    fun currentImages(): Pair<Address, List<Image>>?
    fun selectImagesFrom(addressName: String)
    fun locationImages(): List<AddressLocationImages>
}