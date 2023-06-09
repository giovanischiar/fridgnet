package io.schiar.fridgnet.model.repository

import io.schiar.fridgnet.model.Address
import io.schiar.fridgnet.model.Image

interface PhotosRepository {
    fun subscribeForNewImages(callback: () -> Unit)
    fun currentImages(): Pair<Address, List<Image>>?
}