package io.schiar.fridgnet.model

fun List<Image>.mergeToBoundingBox(): BoundingBox? {
    if (isEmpty()) return null
    val boundingBox = this[0].geoLocation.toBoundingBox()
    return this.fold(initial = boundingBox) { acc, image -> acc + image.geoLocation }
}