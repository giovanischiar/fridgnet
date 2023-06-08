package io.schiar.fridgnet.model.repository.image

import android.annotation.SuppressLint
import android.content.ContentResolver
import android.net.Uri
import androidx.exifinterface.media.ExifInterface
import io.schiar.fridgnet.model.Coordinate

class ImageAndroidDataSource(private val contentResolver: ContentResolver): ImageDataSource {
    override fun extractCoordinate(uri: String): Coordinate {
        val systemURI = Uri.parse(uri)
        contentResolver.openInputStream(systemURI)!!.use { ins ->
            val exifInterface = ExifInterface(ins)
            val latLng = exifInterface.latLong ?: doubleArrayOf(0.0, 0.0)
            return Coordinate(latitude = latLng[0], longitude = latLng[1])
        }
    }

    @SuppressLint("RestrictedApi")
    override fun extractDate(uri: String): Long {
        val systemURI = Uri.parse(uri)
        contentResolver.openInputStream(systemURI)!!.use { ins ->
            val exifInterface = ExifInterface(ins)
            return exifInterface.dateTime ?: 0L
        }
    }
}