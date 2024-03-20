package io.schiar.fridgnet.model.repository

import io.schiar.fridgnet.Log
import io.schiar.fridgnet.model.AdminUnit
import io.schiar.fridgnet.model.GeoLocation
import io.schiar.fridgnet.model.Image
import io.schiar.fridgnet.model.datasource.AdministrativeUnitDataSource
import io.schiar.fridgnet.model.datasource.CurrentAdminUnitDataSource
import io.schiar.fridgnet.model.datasource.ImageDataSource
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.onEach

class PhotosRepository(
    currentAdminUnitDataSource: CurrentAdminUnitDataSource,
    imageDataSource: ImageDataSource,
    administrativeUnitDataSource: AdministrativeUnitDataSource
)  {
    private var _adminUnit: AdminUnit? = null

    @OptIn(ExperimentalCoroutinesApi::class)
    val adminUnit = currentAdminUnitDataSource
        .retrieve()
        .onEach { _adminUnit = it }
        .flatMapLatest {
            val cartographicBoundary = it?.cartographicBoundary
            if (cartographicBoundary == null) {
                return@flatMapLatest flowOf(value = emptyList())
            } else {
                administrativeUnitDataSource.retrieveGeoLocations(
                    administrativeUnit = cartographicBoundary.administrativeUnit,
                    administrativeLevel = cartographicBoundary.administrativeLevel
                )
            }
        }.combine(
            flow = imageDataSource.retrieve(),
            transform = ::combineGeoLocationImages
        ).filterNotNull()

    private fun combineGeoLocationImages(
        geoLocations: List<GeoLocation>, images: List<Image>
    ): AdminUnit? {
        Log.d(
            tag = "",
            msg = "combineGeoLocationImages(" +
                    "geoLocations = $geoLocations, " +
                    "image geoLocations = ${images.map { it.geoLocation }} )"
        )
        return _adminUnit?.with(
            images = images.filter { image -> geoLocations.contains(element = image.geoLocation) }
        )
    }
}