package io.schiar.fridgnet.model.repository

import io.schiar.fridgnet.model.Address
import io.schiar.fridgnet.model.BoundingBox
import io.schiar.fridgnet.model.Image
import io.schiar.fridgnet.model.Location

interface PhotosRepository {
    fun subscribeForNewImages(callback: () -> Unit)
    fun currentImages(): Pair<Address, List<Image>>?
    fun selectedLocation(): Location?
    fun selectedBoundingBox(): BoundingBox?
    fun selectedImagesBoundingBox(): BoundingBox?
}