package io.schiar.fridgnet.view.screen.map.component

import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import io.schiar.fridgnet.view.util.toBitmapDescriptor
import io.schiar.fridgnet.view.util.toLatLng
import io.schiar.fridgnet.view.viewdata.ImageViewData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Collections

@Composable
fun ImagesDrawer(images: List<ImageViewData>) {
    val coroutineScope = rememberCoroutineScope()
    val bitmaps by remember {
        mutableStateOf(Collections.synchronizedMap(mutableMapOf<Uri, BitmapDescriptor>()))
    }
    val jobs = remember { Collections.synchronizedMap(mutableMapOf<Uri, Job>()) }

    images.map { image ->
        val (uri, byteArray, _, geoLocation) = image
        if (!(bitmaps.containsKey(uri) || jobs.containsKey(uri))) {
            jobs[uri] = coroutineScope.launch(Dispatchers.IO) {
                val bitmap = withContext(Dispatchers.IO) {
                    byteArray.toBitmapDescriptor()
                }
                bitmaps[uri] = bitmap
                jobs.remove(uri)
            }
        }
        Marker(
            state = MarkerState(position = geoLocation.toLatLng()),
            icon = bitmaps[uri],
            visible = bitmaps.containsKey(uri)
        )
    }
}