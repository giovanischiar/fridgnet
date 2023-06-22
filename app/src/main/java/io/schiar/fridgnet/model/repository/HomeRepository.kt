package io.schiar.fridgnet.model.repository

import io.schiar.fridgnet.model.AddressLocationImages
import io.schiar.fridgnet.model.AdministrativeUnit

interface HomeRepository {
    fun subscribeForNewAddressAdded(callback: suspend () -> Unit)
    fun subscribeForLocationsReady(callback: suspend () -> Unit)
    suspend fun selectImagesFrom(addressName: String)
    suspend fun locationImages(): List<AddressLocationImages>
    fun changeCurrent(administrativeUnit: AdministrativeUnit)
    suspend fun removeAllImages()
}