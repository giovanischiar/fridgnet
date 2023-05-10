package io.schiar.fridgnet.view.util

import android.content.ContentResolver
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import kotlin.math.roundToInt

class BitmapLoader(private val contentResolver: ContentResolver, private val uri: Uri) {
    fun convert(): BitmapDescriptor {
        val rawBitmap = if (Build.VERSION.SDK_INT < 28) {
            @Suppress("DEPRECATION")
            MediaStore.Images.Media.getBitmap(contentResolver, uri)
        } else {
            val source = ImageDecoder.createSource(contentResolver, uri)
            ImageDecoder.decodeBitmap(source)
        }
        val bitmap = resize(bitmap = rawBitmap)
        return BitmapDescriptorFactory.fromBitmap(bitmap)
    }

    private fun resize(bitmap: Bitmap): Bitmap {
        val width = bitmap.width
        val height = bitmap.height
        val maxWidth = 50f
        val maxHeight = 50f
        val ratioBitmap = width.toFloat() / height.toFloat()
        val ratioMax = 1
        var finalWidth = maxWidth.roundToInt()
        var finalHeight = maxHeight.roundToInt()
        if (ratioMax > ratioBitmap) {
            finalWidth = (maxHeight * ratioBitmap).toInt()
        } else {
            finalHeight = (maxWidth / ratioBitmap).toInt()
        }
        return Bitmap.createScaledBitmap(bitmap, finalWidth, finalHeight, true)
    }
}