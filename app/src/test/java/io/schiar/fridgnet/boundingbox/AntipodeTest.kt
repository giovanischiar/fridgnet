package io.schiar.fridgnet.boundingbox

import io.schiar.fridgnet.model.BoundingBox
import io.schiar.fridgnet.model.Coordinate
import io.schiar.fridgnet.model.centerAntipode
import org.junit.Assert
import org.junit.Test

class AntipodeTest {
    @Test
    fun `Bounding box crossing the Antimeridian with antipode negative`() {
        val boundingBox = BoundingBox(
            southwest = Coordinate(latitude = -10.0, longitude = 90.0),
            northeast = Coordinate(latitude = 10.0, longitude = -170.0)
        )

        Assert.assertEquals(-40.0, boundingBox.centerAntipode(), 0.0)
    }

    @Test
    fun `Bounding box crossing the Antimeridian with antipode positive`() {
        val boundingBox = BoundingBox(
            southwest = Coordinate(latitude = -10.0, longitude = 170.0),
            northeast = Coordinate(latitude = 10.0, longitude = -90.0)
        )

        Assert.assertEquals(40.0, boundingBox.centerAntipode(), 0.0)
    }

    @Test
    fun `Bounding box not crossing the Antimeridian with antipode negative`() {
        val boundingBox = BoundingBox(
            southwest = Coordinate(latitude = -10.0, longitude = 80.0),
            northeast = Coordinate(latitude = 10.0, longitude = 100.0)
        )

        Assert.assertEquals(-90.0, boundingBox.centerAntipode(), 0.0)
    }

    @Test
    fun `Bounding box not crossing the Antimeridian with antipode positive`() {
        val boundingBox = BoundingBox(
            southwest = Coordinate(latitude = -10.0, longitude = -100.0),
            northeast = Coordinate(latitude = 10.0, longitude = -80.0)
        )

        Assert.assertEquals(90.0, boundingBox.centerAntipode(), 0.0)
    }

    @Test
    fun `Antipode crossing the Antimeridian`() {
        val boundingBox = BoundingBox(
            southwest = Coordinate(
                latitude = -10.0,
                longitude = -10.0
            ),
            northeast = Coordinate(
                latitude = 10.0,
                longitude = 10.0
            )
        )

        Assert.assertEquals(-180.0, boundingBox.centerAntipode(), 0.0)
    }
}