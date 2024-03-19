package io.schiar.fridgnet.model.datasource.local

import io.schiar.fridgnet.model.LocationGeoLocation
import io.schiar.fridgnet.model.datasource.CurrentLocationGeoLocationDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

class CurrentLocationGeoLocationDataSource:
    CurrentLocationGeoLocationDataSource {
    private val _locationGeoLocation = MutableStateFlow<LocationGeoLocation?>(
        value = null
    )

    override fun retrieve(): Flow<LocationGeoLocation?> {
        return _locationGeoLocation
    }

    override fun update(locationGeoLocation: LocationGeoLocation) {
        _locationGeoLocation.update { locationGeoLocation }
    }
}