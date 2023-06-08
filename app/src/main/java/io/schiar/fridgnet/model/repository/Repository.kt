package io.schiar.fridgnet.model.repository

import io.schiar.fridgnet.model.BoundingBox
import io.schiar.fridgnet.model.Image
import io.schiar.fridgnet.model.Location
import io.schiar.fridgnet.model.Region

interface Repository {
    suspend fun loadDatabase(onDatabaseLoaded: () -> Unit)

    suspend fun addURIs(uris: List<String>)
    fun subscribeForAddressImageAdded(callback: (address: String, images: List<Image>) -> Unit)
    fun subscribeForLocationsReady(callback: (location: Location) -> Unit)
    fun visibleImages(boundingBox: BoundingBox): List<Image>
    fun visibleRegions(boundingBox: BoundingBox): List<Region>
    fun boundingBoxCities(): BoundingBox?
    fun selectNewLocationFrom(region: Region): Location?
    fun selectImagesFrom(addressName: String): List<Image>?

    suspend fun switchRegion(
        region: Region,
        onCurrentLocationChanged: (location: Location?) -> Unit
    )

    suspend fun switchAll(onCurrentLocationChanged: (location: Location?) -> Unit)
}