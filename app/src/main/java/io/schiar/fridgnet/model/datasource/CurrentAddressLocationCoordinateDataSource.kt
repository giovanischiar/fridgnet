package io.schiar.fridgnet.model.datasource

import io.schiar.fridgnet.model.AddressLocationCoordinate
import kotlinx.coroutines.flow.Flow

interface CurrentAddressLocationCoordinateDataSource {
    fun retrieve(): Flow<AddressLocationCoordinate?>
    fun update(addressLocationCoordinate: AddressLocationCoordinate)
}