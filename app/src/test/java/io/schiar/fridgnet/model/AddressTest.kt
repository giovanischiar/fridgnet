package io.schiar.fridgnet.model

import org.junit.Assert
import org.junit.Test

class AddressTest {
    @Test
    fun `Address with everything null`() {
        val address = Address(
            locality = null,
            subAdminArea = null,
            adminArea = null,
            countryName = null
        )

        val expected = ""
        Assert.assertEquals(expected, address.name())
    }

    @Test
    fun `Address with subAdminArea, adminArea and countryName null`() {
        val address = Address(
            locality = "Sunnyvale",
            subAdminArea = null,
            adminArea = null,
            countryName = null
        )

        val expected = "Sunnyvale"
        Assert.assertEquals(expected, address.name())
    }

    @Test
    fun `Address with locality, adminArea and countryName null`() {
        val address = Address(
            locality = null,
            subAdminArea = "Santa Clara County",
            adminArea = null,
            countryName = null
        )

        val expected = "Santa Clara County"
        Assert.assertEquals(expected, address.name())
    }

    @Test
    fun `Address with AdminArea and countryName null`() {
        val address = Address(
            locality = "Sunnyvale",
            subAdminArea = "Santa Clara County",
            adminArea = null,
            countryName = null
        )

        val expected = "Sunnyvale, Santa Clara County"
        Assert.assertEquals(expected, address.name())
    }

    @Test
    fun `Address with Locality, subAdminArea and countryName null`() {
        val address = Address(
            locality = null,
            subAdminArea = null,
            adminArea = "California",
            countryName = null
        )

        val expected = "California"
        Assert.assertEquals(expected, address.name())
    }

    @Test
    fun `Address with subAdminArea and countryName null`() {
        val address = Address(
            locality = "Sunnyvale",
            subAdminArea = null,
            adminArea = "California",
            countryName = null
        )

        val expected = "Sunnyvale, California"
        Assert.assertEquals(expected, address.name())
    }

    @Test
    fun `Address with countryName null`() {
        val address = Address(
            locality = "Sunnyvale",
            subAdminArea = "Santa Clara County",
            adminArea = "California",
            countryName = null
        )

        val expected = "Sunnyvale, Santa Clara County, California"
        Assert.assertEquals(expected, address.name())
    }

    @Test
    fun `Address with locality, subAdminArea and adminArea null`() {
        val address = Address(
            locality = null,
            subAdminArea = null,
            adminArea = null,
            countryName = "United States"
        )

        val expected = "United States"
        Assert.assertEquals(expected, address.name())
    }

    @Test
    fun `Address with locality and country null`() {
        val address = Address(
            locality = "Sunnyvale",
            subAdminArea = null,
            adminArea = null,
            countryName = "United States"
        )

        val expected = "Sunnyvale, United States"
        Assert.assertEquals(expected, address.name())
    }

    @Test
    fun `Address with locality and adminArea null`() {
        val address = Address(
            locality = null,
            subAdminArea = "Santa Clara County",
            adminArea = null,
            countryName = "United States"
        )

        val expected = "Santa Clara County, United States"
        Assert.assertEquals(expected, address.name())
    }

    @Test
    fun `Address with adminArea null`() {
        val address = Address(
            locality = "Sunnyvale",
            subAdminArea = "Santa Clara County",
            adminArea = null,
            countryName = "United States"
        )

        val expected = "Sunnyvale, Santa Clara County, United States"
        Assert.assertEquals(expected, address.name())
    }

    @Test
    fun `Address with location and subAdminArea null`() {
        val address = Address(
            locality = null,
            subAdminArea = null,
            adminArea = "California",
            countryName = "United States"
        )

        val expected = "California, United States"
        Assert.assertEquals(expected, address.name())
    }

    @Test
    fun `Address with subAdminArea null`() {
        val address = Address(
            locality = "Sunnyvale",
            subAdminArea = null,
            adminArea = "California",
            countryName = "United States"
        )

        val expected = "Sunnyvale, California, United States"
        Assert.assertEquals(expected, address.name())
    }

    @Test
    fun `Address with locality null`() {
        val address = Address(
            locality = null,
            subAdminArea = "Santa Clara County",
            adminArea = "California",
            countryName = "United States"
        )

        val expected = "Santa Clara County, California, United States"
        Assert.assertEquals(expected, address.name())
    }

    @Test
    fun `Address with nothing null`() {
        val address = Address(
            locality = "Sunnyvale",
            subAdminArea = "Santa Clara County",
            adminArea = "California",
            countryName = "United States"
        )

        val expected = "Sunnyvale, Santa Clara County, California, United States"
        Assert.assertEquals(expected, address.name())
    }
}