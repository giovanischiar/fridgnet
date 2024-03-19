package io.schiar.fridgnet.model.repository

import io.schiar.fridgnet.Log
import io.schiar.fridgnet.model.CartographicBoundaryGeoLocation
import io.schiar.fridgnet.model.CartographicBoundaryImages
import io.schiar.fridgnet.model.GeoLocation
import io.schiar.fridgnet.model.Image
import io.schiar.fridgnet.model.datasource.AdministrativeUnitDataSource
import io.schiar.fridgnet.model.datasource.CurrentCartographicBoundaryGeoLocationDataSource
import io.schiar.fridgnet.model.datasource.ImageDataSource
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.onEach

class PhotosRepository(
    currentCartographicBoundaryGeoLocationsDataSource: CurrentCartographicBoundaryGeoLocationDataSource,
    imageDataSource: ImageDataSource,
    administrativeUnitDataSource: AdministrativeUnitDataSource
)  {
    private var cartographicBoundaryGeoLocation: CartographicBoundaryGeoLocation? = null

    @OptIn(ExperimentalCoroutinesApi::class)
    val cartographicBoundaryImages = currentCartographicBoundaryGeoLocationsDataSource
        .retrieve()
        .onEach { cartographicBoundaryGeoLocation = it }
        .flatMapLatest {
            if (it?.cartographicBoundary == null) {
                return@flatMapLatest flowOf(value = emptyList())
            } else {
                administrativeUnitDataSource.retrieveGeoLocations(
                    administrativeUnit = it.cartographicBoundary.administrativeUnit, administrativeLevel = it.cartographicBoundary.administrativeLevel
                )
            }
        }.combine(
            flow = imageDataSource.retrieve(),
            transform = ::combineGeoLocationImages
        ).filterNotNull()

    private fun combineGeoLocationImages(
        geoLocations: List<GeoLocation>, images: List<Image>
    ): CartographicBoundaryImages? {
        Log.d("", "combineGeoLocationImages(geoLocations = $geoLocations, image geoLocations = ${images.map { it.geoLocation }} )")
        return CartographicBoundaryImages(
            cartographicBoundary = cartographicBoundaryGeoLocation?.cartographicBoundary ?: return null,
            images = images.filter { image -> geoLocations.contains(element = image.geoLocation) }
        )
    }
}