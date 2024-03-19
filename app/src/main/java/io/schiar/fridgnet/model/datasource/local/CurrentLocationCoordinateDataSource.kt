package io.schiar.fridgnet.model.datasource.local

import io.schiar.fridgnet.model.LocationCoordinate
import io.schiar.fridgnet.model.datasource.CurrentLocationCoordinateDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

class CurrentLocationCoordinateDataSource:
    CurrentLocationCoordinateDataSource {
    private val _addressLocationCoordinate = MutableStateFlow<LocationCoordinate?>(
        value = null
    )

    override fun retrieve(): Flow<LocationCoordinate?> {
        return _addressLocationCoordinate
    }

    override fun update(locationCoordinate: LocationCoordinate) {
        _addressLocationCoordinate.update { locationCoordinate }
    }
}