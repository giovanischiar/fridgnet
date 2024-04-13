package io.schiar.fridgnet.model

/**
 * Calculates a bounding box that encompasses the geo locations of all images in the collection.
 *
 * @return a BoundingBox containing all geo locations, or null if the collection is empty.
 */
fun Collection<Image>.mergeToBoundingBox(): BoundingBox? {
    if (isEmpty()) return null
    val boundingBox = this.first().geoLocation.toBoundingBox()
    return this.fold(initial = boundingBox) { acc, image -> acc + image.geoLocation }
}