package io.schiar.fridgnet.model.repository.listeners

interface OnNewImageAddedListener {
    suspend fun onNewImageAdded()
}