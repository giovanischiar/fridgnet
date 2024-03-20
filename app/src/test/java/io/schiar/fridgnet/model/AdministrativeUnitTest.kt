package io.schiar.fridgnet.model

import org.junit.Assert
import org.junit.Test

class AdministrativeUnitNameTest {
    @Test
    fun `AdministrativeUnitName with everything null`() {
        val administrativeUnitName = AdministrativeUnitName(
            locality = null,
            subAdminArea = null,
            adminArea = null,
            countryName = null
        )

        val expected = ""
        Assert.assertEquals(expected, administrativeUnitName.name())
    }

    @Test
    fun `AdministrativeUnitName with subAdminArea, adminArea and countryName null`() {
        val administrativeUnitName = AdministrativeUnitName(
            locality = "Sunnyvale",
            subAdminArea = null,
            adminArea = null,
            countryName = null
        )

        val expected = "Sunnyvale"
        Assert.assertEquals(expected, administrativeUnitName.name())
    }

    @Test
    fun `AdministrativeUnitName with locality, adminArea and countryName null`() {
        val administrativeUnitName = AdministrativeUnitName(
            locality = null,
            subAdminArea = "Santa Clara County",
            adminArea = null,
            countryName = null
        )

        val expected = "Santa Clara County"
        Assert.assertEquals(expected, administrativeUnitName.name())
    }

    @Test
    fun `AdministrativeUnitName with AdminArea and countryName null`() {
        val administrativeUnitName = AdministrativeUnitName(
            locality = "Sunnyvale",
            subAdminArea = "Santa Clara County",
            adminArea = null,
            countryName = null
        )

        val expected = "Sunnyvale, Santa Clara County"
        Assert.assertEquals(expected, administrativeUnitName.name())
    }

    @Test
    fun `AdministrativeUnitName with Locality, subAdminArea and countryName null`() {
        val administrativeUnitName = AdministrativeUnitName(
            locality = null,
            subAdminArea = null,
            adminArea = "California",
            countryName = null
        )

        val expected = "California"
        Assert.assertEquals(expected, administrativeUnitName.name())
    }

    @Test
    fun `AdministrativeUnitName with subAdminArea and countryName null`() {
        val administrativeUnitName = AdministrativeUnitName(
            locality = "Sunnyvale",
            subAdminArea = null,
            adminArea = "California",
            countryName = null
        )

        val expected = "Sunnyvale, California"
        Assert.assertEquals(expected, administrativeUnitName.name())
    }

    @Test
    fun `AdministrativeUnitName with countryName null`() {
        val administrativeUnitName = AdministrativeUnitName(
            locality = "Sunnyvale",
            subAdminArea = "Santa Clara County",
            adminArea = "California",
            countryName = null
        )

        val expected = "Sunnyvale, Santa Clara County, California"
        Assert.assertEquals(expected, administrativeUnitName.name())
    }

    @Test
    fun `AdministrativeUnitName with locality, subAdminArea and adminArea null`() {
        val administrativeUnitName = AdministrativeUnitName(
            locality = null,
            subAdminArea = null,
            adminArea = null,
            countryName = "United States"
        )

        val expected = "United States"
        Assert.assertEquals(expected, administrativeUnitName.name())
    }

    @Test
    fun `AdministrativeUnitName with locality and country null`() {
        val administrativeUnitName = AdministrativeUnitName(
            locality = "Sunnyvale",
            subAdminArea = null,
            adminArea = null,
            countryName = "United States"
        )

        val expected = "Sunnyvale, United States"
        Assert.assertEquals(expected, administrativeUnitName.name())
    }

    @Test
    fun `AdministrativeUnitName with locality and adminArea null`() {
        val administrativeUnitName = AdministrativeUnitName(
            locality = null,
            subAdminArea = "Santa Clara County",
            adminArea = null,
            countryName = "United States"
        )

        val expected = "Santa Clara County, United States"
        Assert.assertEquals(expected, administrativeUnitName.name())
    }

    @Test
    fun `AdministrativeUnitName with adminArea null`() {
        val administrativeUnitName = AdministrativeUnitName(
            locality = "Sunnyvale",
            subAdminArea = "Santa Clara County",
            adminArea = null,
            countryName = "United States"
        )

        val expected = "Sunnyvale, Santa Clara County, United States"
        Assert.assertEquals(expected, administrativeUnitName.name())
    }

    @Test
    fun `AdministrativeUnitName with locality and subAdminArea null`() {
        val administrativeUnitName = AdministrativeUnitName(
            locality = null,
            subAdminArea = null,
            adminArea = "California",
            countryName = "United States"
        )

        val expected = "California, United States"
        Assert.assertEquals(expected, administrativeUnitName.name())
    }

    @Test
    fun `AdministrativeUnitName with subAdminArea null`() {
        val administrativeUnitName = AdministrativeUnitName(
            locality = "Sunnyvale",
            subAdminArea = null,
            adminArea = "California",
            countryName = "United States"
        )

        val expected = "Sunnyvale, California, United States"
        Assert.assertEquals(expected, administrativeUnitName.name())
    }

    @Test
    fun `AdministrativeUnitName with locality null`() {
        val administrativeUnitName = AdministrativeUnitName(
            locality = null,
            subAdminArea = "Santa Clara County",
            adminArea = "California",
            countryName = "United States"
        )

        val expected = "Santa Clara County, California, United States"
        Assert.assertEquals(expected, administrativeUnitName.name())
    }

    @Test
    fun `AdministrativeUnitName with nothing null`() {
        val administrativeUnitName = AdministrativeUnitName(
            locality = "Sunnyvale",
            subAdminArea = "Santa Clara County",
            adminArea = "California",
            countryName = "United States"
        )

        val expected = "Sunnyvale, Santa Clara County, California, United States"
        Assert.assertEquals(expected, administrativeUnitName.name())
    }
}