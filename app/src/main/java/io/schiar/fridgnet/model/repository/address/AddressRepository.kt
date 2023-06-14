package io.schiar.fridgnet.model.repository.address

import io.schiar.fridgnet.model.Address
import io.schiar.fridgnet.model.Coordinate

interface AddressRepository {
    suspend fun setup()
    suspend fun fetchAddressBy(coordinate: Coordinate): Address?
}