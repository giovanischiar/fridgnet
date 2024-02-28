package io.schiar.fridgnet.model.repository.listeners

import io.schiar.fridgnet.model.Image

interface OnImageAddedListener {
    suspend fun onImageAdded(image: Image)
}