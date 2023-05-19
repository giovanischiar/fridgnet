package io.schiar.fridgnet.boundingbox

import io.schiar.fridgnet.model.BoundingBox
import io.schiar.fridgnet.model.Coordinate
import org.junit.Assert
import org.junit.Test

class AntimeridianTest {

    @Test
    fun `Big Bounding Box not containing the antimeridian`() {
        val bounds = BoundingBox(
            southwest = Coordinate(latitude = 0.0, longitude = -90.0),
            northeast = Coordinate(latitude = 0.0, longitude = 90.0)
        )

        Assert.assertFalse(bounds.containsAntimeridian())
    }

    @Test
    fun `Big Bounding Box containing the antimeridian`() {
        val bounds = BoundingBox(
            southwest = Coordinate(latitude = 0.0, longitude = -90.000001),
            northeast = Coordinate(latitude = 0.0, longitude = 90.0)
        )

        Assert.assertTrue(bounds.containsAntimeridian())
    }

    @Test
    fun `Big Bounding Box with southwest on the antimeridian not containing it`() {
        val bounds = BoundingBox(
            southwest = Coordinate(latitude = 0.0, longitude = -180.0),
            northeast = Coordinate(latitude = 0.0, longitude = 0.0)
        )

        Assert.assertFalse(bounds.containsAntimeridian())
    }

    @Test
    fun `Big Bounding Box with southwest on the antimeridian containing it`() {
        val bounds = BoundingBox(
            southwest = Coordinate(latitude = 0.0, longitude = -180.0),
            northeast = Coordinate(latitude = 0.0, longitude = 0.000001)
        )

        Assert.assertTrue(bounds.containsAntimeridian())
    }
}