package io.schiar.fridgnet.model.repository

import io.schiar.fridgnet.model.AddressLocationImages

interface HomeRepository {
    fun subscribeForAddressImageAdded(callback: () -> Unit)
    fun subscribeForLocationsReady(callback: () -> Unit)
    fun selectImagesFrom(addressName: String)
    fun locationImages(): List<AddressLocationImages>
}