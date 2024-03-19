package io.schiar.fridgnet.model.datasource

import io.schiar.fridgnet.model.LocationGeoLocation
import kotlinx.coroutines.flow.Flow

interface CurrentLocationGeoLocationDataSource {
    fun retrieve(): Flow<LocationGeoLocation?>
    fun update(locationGeoLocation: LocationGeoLocation)
}