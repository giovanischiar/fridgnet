package io.schiar.fridgnet.view.util

import android.content.ContentResolver
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import kotlin.math.roundToInt

class BitmapLoader(private val contentResolver: ContentResolver, private val uri: Uri) {
    fun convert(): BitmapDescriptor? {
        val options = BitmapFactory.Options()
        options.inSampleSize = 4
        val inputStream = contentResolver.openInputStream(uri)
        val rawBitmap = BitmapFactory.decodeStream(
            inputStream,
            null,
            options
        ) ?: return null
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