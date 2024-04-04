package io.schiar.fridgnet.view.shared.util.debug

import android.util.Log
import com.google.maps.android.compose.CameraPositionState
import io.schiar.fridgnet.view.shared.viewdata.RegionViewData

class BoundsTestCreator {
    fun generateTest(
        cameraPositionState: CameraPositionState,
        visibleRegions: List<RegionViewData>
    ) {
        if (visibleRegions.isNotEmpty()) {
            Log.d("BoundingBoxTest", "package io.schiar.fridgnet.boundingbox")
            Log.d("BoundingBoxTest", "")
            Log.d("BoundingBoxTest", "import io.schiar.fridgnet.model.BoundingBox")
            Log.d("BoundingBoxTest", "import io.schiar.fridgnet.model.GeoLocation")
            Log.d("BoundingBoxTest", "import io.schiar.fridgnet.model.contains")
            Log.d("BoundingBoxTest", "import org.junit.Assert")
            Log.d("BoundingBoxTest", "import org.junit.Test")
            Log.d("BoundingBoxTest", "")

            Log.d("BoundingBoxTest", "class PolygonsAppCreatedTest {")
            if (cameraPositionState.projection?.visibleRegion != null) {
                val boundingBox = cameraPositionState.projection?.visibleRegion!!.latLngBounds
                val southwest = boundingBox.southwest
                val northeast = boundingBox.northeast
                val southwestStr =
                    "GeoLocation(" +
                            "latitude = ${southwest.latitude}, longitude = ${southwest.longitude}" +
                    ")"
                val northeastStr =
                    "GeoLocation(" +
                            "latitude = ${northeast.latitude}, longitude = ${northeast.longitude}" +
                    ")"
                val boundingBoxStr =
                    "BoundingBox(\n\t\t" +
                            "southwest = $southwestStr,\n\t\tnortheast = $northeastStr\n\t" +
                    ")"
                Log.d("BoundingBoxTest", "\tprivate val boundingBox = $southwestStr")
                Log.d("BoundingBoxTest", "")
            }

            Log.d(
                "BoundingBoxTest",
                visibleRegions.map { it.boundingBox }.mapIndexed { index, boundingBoxViewData ->
                    val (southwest, northeast) = boundingBoxViewData
                    val southwestStr =
                        "GeoLocation(" +
                                "latitude = ${southwest.latitude}, " +
                                "longitude = ${southwest.longitude}" +
                        ")"
                    val northeastStr =
                        "GeoLocation(" +
                                "latitude = ${northeast.latitude}, " +
                                "longitude = ${northeast.longitude}" +
                        ")"

                    "\t@Test\n\tfun `Polygons app generated are inside bounding box $index`() {" +
                            "\n\t\tval polygon$index = BoundingBox(\n" +
                                  "\t\t\tsouthwest = $southwestStr,\n" +
                                  "\t\t\tnortheast = $northeastStr\n\t\t" +
                            ")\n\n\t\t" +
                            "Assert.assertFalse(boundingBox.contains(polygon$index))\n\t" +
                      "}"
                }.joinToString("\n\n")
            )

            Log.d("BoundingBoxTest", "}")
        }
    }
}