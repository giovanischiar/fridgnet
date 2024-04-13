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
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import javax.inject.Inject
import kotlin.math.roundToInt

/**
 *  An implementation of the [ImageRetriever] interface that utilizes the Android system's
 *  [ContentResolver] to retrieve and process image data from provided URIs. This class operates
 *  asynchronously and emits successfully processed images through a returned [Flow] of [Image]
 *  objects.
 *
 *  **Threading Model:** This class uses coroutines for asynchronous image retrieval and processing.
 *  It's recommended to call the `retrieve` function from a background coroutine to avoid blocking
 *  the main thread.
 *
 *  **Assumptions:** This class assumes the provided URIs are content URIs (e.g., from the device's
 *  media store) and not direct file paths.
 */
class ImageAndroidRetriever @Inject constructor(
    private val contentResolver: ContentResolver
) : ImageRetriever {

    /**
     * Uses the Android system ([ContentResolver]) to asynchronously retrieve and convert a list of
     * URIs into corresponding [Image] objects. Each successful image conversion will be emitted
     * through the returned [Flow] of [Image].
     *
     * @param uris a list of string representations of image URIs.
     * @return a [Flow] that emits successfully retrieved and processed [Image] objects.
     *
     * If an error occurs while processing a specific URI, processing continues for the remaining
     * URIs in the list. Errors are logged using the class's internal logging mechanism.
     */
    override suspend fun retrieve(uris: List<String>): Flow<Image> = flow {
        uris.forEach { uri ->
            val systemURI = Uri.parse(uri)
            val inputStreamOpened = contentResolver.openInputStream(systemURI)
            if (inputStreamOpened == null) {
                log(msg = "Couldn't open the input stream for image of uri $uri")
                return@forEach
            }
            inputStreamOpened.use { ins ->
                val exifInterface = ExifInterface(ins)
                val latLng = exifInterface.latLong
                if (latLng == null) {
                    log(msg = "Image of $uri doesn't have geo location")
                    return@forEach
                }
                val geoLocation = GeoLocation(latitude = latLng[0], longitude = latLng[1])
                @SuppressLint("RestrictedApi")
                val date = exifInterface.dateTime ?: 0L
                val byteArray = withContext(Dispatchers.Default) { systemURI.toByteArray() }
                emit(value = Image(
                    uri = uri,
                    byteArray = byteArray,
                    date = date,
                    geoLocation = geoLocation
                ))
            }
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
            log(msg = "Caught exception of image of uri $this: $e")
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

    private fun log(msg: String) {
        val methodName = Thread.currentThread().stackTrace[3].methodName
        Log.d(tag = "ImageAndroidRetriever.$methodName", msg = msg)
    }
}