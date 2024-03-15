package io.schiar.fridgnet.model.repository

import io.schiar.fridgnet.Log
import io.schiar.fridgnet.model.LocationImages
import io.schiar.fridgnet.model.Coordinate
import io.schiar.fridgnet.model.Image
import io.schiar.fridgnet.model.LocationCoordinate
import io.schiar.fridgnet.model.datasource.AddressDataSource
import io.schiar.fridgnet.model.datasource.CurrentLocationCoordinateDataSource
import io.schiar.fridgnet.model.datasource.ImageDataSource
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.onEach

class PhotosRepository(
    currentAddressLocationCoordinateDataSource: CurrentLocationCoordinateDataSource,
    imageDataSource: ImageDataSource,
    addressCoordinatesDataSource: AddressDataSource
)  {
    private var addressLocationCoordinate: LocationCoordinate? = null

    @OptIn(ExperimentalCoroutinesApi::class)
    val locationImages = currentAddressLocationCoordinateDataSource
        .retrieve()
        .onEach { addressLocationCoordinate = it }
        .flatMapLatest {
            if (it?.location == null) {
                return@flatMapLatest flowOf(value = emptyList())
            } else {
                addressCoordinatesDataSource.retrieveCoordinates(
                    address = it.location.address, administrativeUnit = it.location.administrativeUnit
                )
            }
        }.combine(
            flow = imageDataSource.retrieve(),
            transform = ::combineCoordinatesImages
        ).filterNotNull()

    private fun combineCoordinatesImages(
        coordinates: List<Coordinate>, images: List<Image>
    ): LocationImages? {
        Log.d("", "combineCoordinatesImages(coordinates = $coordinates, image coordinates = ${images.map { it.coordinate }} )")
        return LocationImages(
            location = addressLocationCoordinate?.location ?: return null,
            images = images.filter { image -> coordinates.contains(element = image.coordinate) }
        )
    }
}