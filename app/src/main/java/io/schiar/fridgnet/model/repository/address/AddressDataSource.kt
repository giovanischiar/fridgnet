package io.schiar.fridgnet.model.repository.address

import io.schiar.fridgnet.model.Address
import io.schiar.fridgnet.model.Coordinate

interface AddressDataSource {
    fun fetchAddressBy(coordinate: Coordinate): Address?
}