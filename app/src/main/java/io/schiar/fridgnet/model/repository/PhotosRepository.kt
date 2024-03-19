package io.schiar.fridgnet.model.repository

import io.schiar.fridgnet.Log
import io.schiar.fridgnet.model.GeoLocation
import io.schiar.fridgnet.model.Image
import io.schiar.fridgnet.model.LocationGeoLocation
import io.schiar.fridgnet.model.LocationImages
import io.schiar.fridgnet.model.datasource.AddressDataSource
import io.schiar.fridgnet.model.datasource.CurrentLocationGeoLocationDataSource
import io.schiar.fridgnet.model.datasource.ImageDataSource
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.onEach

class PhotosRepository(
    currentAddressLocationsGeoLocationsDataSource: CurrentLocationGeoLocationDataSource,
    imageDataSource: ImageDataSource,
    addressLocationsGeoLocationsDataSource: AddressDataSource
)  {
    private var locationGeoLocation: LocationGeoLocation? = null

    @OptIn(ExperimentalCoroutinesApi::class)
    val locationImages = currentAddressLocationsGeoLocationsDataSource
        .retrieve()
        .onEach { locationGeoLocation = it }
        .flatMapLatest {
            if (it?.location == null) {
                return@flatMapLatest flowOf(value = emptyList())
            } else {
                addressLocationsGeoLocationsDataSource.retrieveGeoLocations(
                    address = it.location.address, administrativeLevel = it.location.administrativeLevel
                )
            }
        }.combine(
            flow = imageDataSource.retrieve(),
            transform = ::combineGeoLocationImages
        ).filterNotNull()

    private fun combineGeoLocationImages(
        geoLocations: List<GeoLocation>, images: List<Image>
    ): LocationImages? {
        Log.d("", "combineGeoLocationImages(geoLocations = $geoLocations, image geoLocations = ${images.map { it.geoLocation }} )")
        return LocationImages(
            location = locationGeoLocation?.location ?: return null,
            images = images.filter { image -> geoLocations.contains(element = image.geoLocation) }
        )
    }
}