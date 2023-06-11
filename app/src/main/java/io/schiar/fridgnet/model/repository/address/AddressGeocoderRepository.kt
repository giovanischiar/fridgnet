package io.schiar.fridgnet.model.repository.address

import io.schiar.fridgnet.model.Address
import io.schiar.fridgnet.model.Coordinate
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

class AddressGeocoderRepository(private val dataSource: AddressDataSource): AddressRepository {
    override suspend fun getAddressFrom(
        coordinate: Coordinate,
        onReady: suspend (address: Address) -> Unit)
    {
        coroutineScope {
            launch(Dispatchers.IO) {
                val address = dataSource.convertToAddress(coordinate) ?: return@launch
                address.allAddresses().forEach { onReady(it) }
            }
        }
    }
}