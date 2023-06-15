package io.schiar.fridgnet.model.repository

import io.schiar.fridgnet.model.AddressLocationImages
import io.schiar.fridgnet.model.AdministrativeUnit

interface HomeRepository {
    fun subscribeForAddressImageAdded(callback: () -> Unit)
    fun subscribeForLocationsReady(callback: () -> Unit)
    fun selectImagesFrom(addressName: String)
    fun locationImages(): List<AddressLocationImages>
    fun changeCurrent(administrativeUnit: AdministrativeUnit)
    suspend fun removeAllImages()
}