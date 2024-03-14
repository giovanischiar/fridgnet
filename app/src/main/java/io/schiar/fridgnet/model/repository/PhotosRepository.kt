package io.schiar.fridgnet.model.repository

import io.schiar.fridgnet.Log
import io.schiar.fridgnet.model.AddressLocationCoordinate
import io.schiar.fridgnet.model.AddressLocationImages
import io.schiar.fridgnet.model.Coordinate
import io.schiar.fridgnet.model.Image
import io.schiar.fridgnet.model.datasource.AddressDataSource
import io.schiar.fridgnet.model.datasource.CurrentAddressLocationCoordinateDataSource
import io.schiar.fridgnet.model.datasource.ImageDataSource
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.onEach

class PhotosRepository(
    currentAddressLocationCoordinateDataSource: CurrentAddressLocationCoordinateDataSource,
    imageDataSource: ImageDataSource,
    addressCoordinatesDataSource: AddressDataSource
)  {
    private var addressLocationCoordinate: AddressLocationCoordinate? = null

    @OptIn(ExperimentalCoroutinesApi::class)
    val addressLocationImages = currentAddressLocationCoordinateDataSource
        .retrieve()
        .onEach { addressLocationCoordinate = it }
        .flatMapLatest {
            if (it?.address == null) {
                return@flatMapLatest flowOf(value = emptyList())
            } else {
                addressCoordinatesDataSource.retrieveCoordinates(address = it.address)
            }
        }.combine(
            flow = imageDataSource.retrieve(),
            transform = ::combineCoordinatesImages
        ).filterNotNull()

    private fun combineCoordinatesImages(
        coordinates: List<Coordinate>, images: List<Image>
    ): AddressLocationImages? {
        Log.d("", "combineCoordinatesImages(coordinates = $coordinates, image coordinates = ${images.map { it.coordinate }} )")
        return AddressLocationImages(
            address = addressLocationCoordinate?.address ?: return null,
            location = addressLocationCoordinate?.location ?: return null,
            images = images.filter { image -> coordinates.contains(element = image.coordinate) }
        )
    }
}