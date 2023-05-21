package io.schiar.fridgnet.model

import org.junit.Assert
import org.junit.Test

class CoordinateTest {
    @Test
    fun `Biggest coordinate possible that cross the antimeridian`() {
        val coordinate = Coordinate(latitude = 0.0, longitude = -90.000001)
        val other = 90.0

        Assert.assertTrue(coordinate.wasAntimeridianCrossed(other = other))
    }

    @Test
    fun `Biggest coordinate possible that not cross the antimeridian`() {
        val coordinate = Coordinate(latitude = 0, longitude = 90)
        val other = 90.0

        Assert.assertFalse(coordinate.wasAntimeridianCrossed(other = other))
    }
}