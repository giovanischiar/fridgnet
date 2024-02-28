package io.schiar.fridgnet.model.repository.listeners

import io.schiar.fridgnet.model.Address

interface OnAddressReadyListener {
    suspend fun onAddressReady(address: Address)
}