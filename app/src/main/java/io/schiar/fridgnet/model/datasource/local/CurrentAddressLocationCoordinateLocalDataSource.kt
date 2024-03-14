package io.schiar.fridgnet.model.datasource.local

import io.schiar.fridgnet.model.AddressLocationCoordinate
import io.schiar.fridgnet.model.datasource.CurrentAddressLocationCoordinateDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

class CurrentAddressLocationCoordinateLocalDataSource: CurrentAddressLocationCoordinateDataSource {
    private val _addressLocationCoordinate = MutableStateFlow<AddressLocationCoordinate?>(
        value = null
    )

    override fun retrieve(): Flow<AddressLocationCoordinate?> {
        return _addressLocationCoordinate
    }

    override fun update(addressLocationCoordinate: AddressLocationCoordinate) {
        _addressLocationCoordinate.update { addressLocationCoordinate }
    }
}