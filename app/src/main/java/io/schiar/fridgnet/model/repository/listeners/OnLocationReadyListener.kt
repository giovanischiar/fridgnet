package io.schiar.fridgnet.model.repository.listeners

interface OnLocationReadyListener {
    suspend fun onLocationReady()
}