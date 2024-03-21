package io.schiar.fridgnet.model

fun Collection<Image>.mergeToBoundingBox(): BoundingBox? {
    if (isEmpty()) return null
    val boundingBox = this.first().geoLocation.toBoundingBox()
    return this.fold(initial = boundingBox) { acc, image -> acc + image.geoLocation }
}