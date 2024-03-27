package io.schiar.fridgnet.model.repository

import io.schiar.fridgnet.Log
import io.schiar.fridgnet.model.AdministrativeUnit
import io.schiar.fridgnet.model.GeoLocation
import io.schiar.fridgnet.model.Image
import io.schiar.fridgnet.model.datasource.AdministrativeUnitNameDataSource
import io.schiar.fridgnet.model.datasource.CurrentAdministrativeUnitDataSource
import io.schiar.fridgnet.model.datasource.ImageDataSource
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

class PhotosRepository @Inject constructor(
    currentAdministrativeUnitDataSource: CurrentAdministrativeUnitDataSource,
    imageDataSource: ImageDataSource,
    administrativeUnitNameService: AdministrativeUnitNameDataSource
)  {
    private var _administrativeUnit: AdministrativeUnit? = null

    @OptIn(ExperimentalCoroutinesApi::class)
    val administrativeUnit = currentAdministrativeUnitDataSource
        .retrieve()
        .onEach { _administrativeUnit = it }
        .flatMapLatest {
            val cartographicBoundary = it?.cartographicBoundary
            if (cartographicBoundary == null) {
                return@flatMapLatest flowOf(value = emptyList())
            } else {
                administrativeUnitNameService.retrieveGeoLocations(
                    administrativeUnitName = cartographicBoundary.administrativeUnitName,
                    administrativeLevel = cartographicBoundary.administrativeLevel
                )
            }
        }.combine(
            flow = imageDataSource.retrieve(),
            transform = ::combineGeoLocationImages
        ).filterNotNull()

    private fun combineGeoLocationImages(
        geoLocations: List<GeoLocation>, images: List<Image>
    ): AdministrativeUnit? {
        Log.d(
            tag = "",
            msg = "combineGeoLocationImages(" +
                    "geoLocations = $geoLocations, " +
                    "image geoLocations = ${images.map { it.geoLocation }} )"
        )
        return _administrativeUnit?.with(
            images = images.filter { image -> geoLocations.contains(element = image.geoLocation) }
        )
    }
}