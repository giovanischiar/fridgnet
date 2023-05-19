package io.schiar.fridgnet.boundingbox

import io.schiar.fridgnet.model.BoundingBox
import io.schiar.fridgnet.model.Coordinate
import io.schiar.fridgnet.model.centerLongitude
import org.junit.Assert
import org.junit.Test

class CenterTest {
    @Test
    fun `Bounding box crossing the Antimeridian with the center shifted west of antimeridian`() {
        val boundingBox = BoundingBox(
            southwest = Coordinate(latitude = -10.0, longitude = 90.0),
            northeast = Coordinate(latitude = 10.0, longitude = -170.0)
        )

        Assert.assertEquals(140.0, boundingBox.centerLongitude(), 0.0)
    }

    @Test
    fun `Bounding box crossing the Antimeridian with the center shifted east of antimeridian`() {
        val boundingBox = BoundingBox(
            southwest = Coordinate(latitude = -10.0, longitude = 170.0),
            northeast = Coordinate(latitude = 10.0, longitude = -100.0)
        )

        Assert.assertEquals(-145.0, boundingBox.centerLongitude(), 0.0)
    }

    @Test
    fun `Bounding box crossing the Antimeridian 2`() {
        val boundingBox = BoundingBox(
            southwest = Coordinate(latitude = -10.0, longitude = 100.0),
            northeast = Coordinate(latitude = 10.0, longitude = -150.0)
        )

        Assert.assertEquals(155.0, boundingBox.centerLongitude(), 0.0)
    }

    @Test
    fun `Bounding box crossing the Antimeridian with the Antimeridian as a center`() {
        val boundingBox = BoundingBox(
            southwest = Coordinate(latitude = -10.0, longitude = 160.0),
            northeast = Coordinate(latitude = 10.0, longitude = -160.0)
        )

        Assert.assertEquals(-180.0, boundingBox.centerLongitude(), 0.0)
    }
}