package io.schiar.fridgnet.model

import org.junit.Assert
import org.junit.Test

class GeoLocationTest {
    @Test
    fun `Biggest geo location possible that cross the antimeridian`() {
        val geoLocation = GeoLocation(latitude = 0.0, longitude = -90.000001)
        val other = 90.0

        Assert.assertTrue(geoLocation.wasAntimeridianCrossed(other = other))
    }

    @Test
    fun `Biggest geo location possible that not cross the antimeridian`() {
        val geoLocation = GeoLocation(latitude = 0, longitude = 90)
        val other = 90.0

        Assert.assertFalse(geoLocation.wasAntimeridianCrossed(other = other))
    }
}