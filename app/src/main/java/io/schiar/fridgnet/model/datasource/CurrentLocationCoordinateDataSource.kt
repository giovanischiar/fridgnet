package io.schiar.fridgnet.model.datasource

import io.schiar.fridgnet.model.LocationCoordinate
import kotlinx.coroutines.flow.Flow

interface CurrentLocationCoordinateDataSource {
    fun retrieve(): Flow<LocationCoordinate?>
    fun update(addressLocationCoordinate: LocationCoordinate)
}