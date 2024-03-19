package io.schiar.fridgnet.library.android

import android.annotation.SuppressLint
import android.content.ContentResolver
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.exifinterface.media.ExifInterface
import io.schiar.fridgnet.Log
import io.schiar.fridgnet.model.GeoLocation
import io.schiar.fridgnet.model.Image
import io.schiar.fridgnet.model.datasource.retriever.ImageRetriever
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import kotlin.math.roundToInt

class ImageAndroidRetriever(private val contentResolver: ContentResolver) : ImageRetriever {
    override suspend fun retrieve(uri: String): Image? {
        val systemURI = Uri.parse(uri)
        (contentResolver.openInputStream(systemURI) ?: return null).use { ins ->
            val exifInterface = ExifInterface(ins)
            val latLng = exifInterface.latLong ?: return null
            val geoLocation = GeoLocation(latitude = latLng[0], longitude = latLng[1])

            @SuppressLint("RestrictedApi")
            val date = exifInterface.dateTime ?: 0L
            val byteArray = withContext(Dispatchers.Default) { systemURI.toByteArray() }
            return Image(
                uri = uri,
                byteArray = byteArray,
                date = date,
                geoLocation = geoLocation
            )
        }
    }

    private fun Uri.toByteArray(): ByteArray {
        val stream = ByteArrayOutputStream()
        this.toResizedBitmap()?.compress(Bitmap.CompressFormat.PNG, 0, stream)
        return stream.toByteArray()
    }

    private fun Uri.toResizedBitmap(): Bitmap? {
        val rawBitmap = try {
            val options = BitmapFactory.Options()
            options.inSampleSize = 4
            val inputStream = contentResolver.openInputStream(this)
            BitmapFactory.decodeStream(
                inputStream,
                null,
                options
            )
        } catch (e: Exception) {
            Log.d("Bitmap Loader", "Exception! $e")
            null
        } ?: return null
        return rawBitmap.resize()
    }

    private fun Bitmap.resize(): Bitmap {
        val width = this.width
        val height = this.height
        val maxWidth = 100f
        val maxHeight = 100f
        val ratioBitmap = width.toFloat() / height.toFloat()
        val ratioMax = 1
        var finalWidth = maxWidth.roundToInt()
        var finalHeight = maxHeight.roundToInt()
        if (ratioMax > ratioBitmap) {
            finalWidth = (maxHeight * ratioBitmap).toInt()
        } else {
            finalHeight = (maxWidth / ratioBitmap).toInt()
        }
        return Bitmap.createScaledBitmap(this, finalWidth, finalHeight, true)
    }
}