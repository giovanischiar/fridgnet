package io.schiar.fridgnet.model.repository.image

import android.annotation.SuppressLint
import android.content.ContentResolver
import android.net.Uri
import androidx.exifinterface.media.ExifInterface
import io.schiar.fridgnet.model.Coordinate
import io.schiar.fridgnet.model.Image

class ImageAndroidDataSource(private val contentResolver: ContentResolver): ImageDataSource {
    override suspend fun fetchImageBy(uri: String): Image? {
        val systemURI = Uri.parse(uri)
        (contentResolver.openInputStream(systemURI)?: return null).use { ins ->
            val exifInterface = ExifInterface(ins)
            val latLng = exifInterface.latLong ?: return null
            val coordinate = Coordinate(latitude = latLng[0], longitude = latLng[1])
            @SuppressLint("RestrictedApi")
            val date = exifInterface.dateTime ?: 0L
            return Image(uri = uri, date = date, coordinate = coordinate)
        }
    }
}