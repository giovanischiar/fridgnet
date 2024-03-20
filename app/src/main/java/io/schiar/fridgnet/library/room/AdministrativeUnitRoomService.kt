package io.schiar.fridgnet.library.room

import io.schiar.fridgnet.model.AdministrativeLevel
import io.schiar.fridgnet.model.AdministrativeLevel.CITY
import io.schiar.fridgnet.model.AdministrativeLevel.COUNTRY
import io.schiar.fridgnet.model.AdministrativeLevel.COUNTY
import io.schiar.fridgnet.model.AdministrativeLevel.STATE
import io.schiar.fridgnet.model.AdministrativeUnit
import io.schiar.fridgnet.model.CartographicBoundary
import io.schiar.fridgnet.model.GeoLocation
import io.schiar.fridgnet.model.service.AdministrativeUnitService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class AdministrativeUnitRoomService(
    private val administrativeUnitDAO: AdministrativeUnitDAO
) : AdministrativeUnitService {
    override fun retrieveGeoLocations(
        administrativeUnit: AdministrativeUnit, administrativeLevel: AdministrativeLevel
    ): Flow<List<GeoLocation>> {
        val (_, locality, subAdminArea, adminArea, countryName) = administrativeUnit
        return when(administrativeLevel) {
            CITY -> administrativeUnitDAO.selectGeoLocations(
                locality = locality,
                subAdminArea = subAdminArea,
                adminArea = adminArea,
                countryName = countryName
            )
            COUNTY -> administrativeUnitDAO.selectGeoLocations(
                subAdminArea = subAdminArea, adminArea = adminArea, countryName = countryName
            )
            STATE -> {
                administrativeUnitDAO.selectGeoLocations(
                    adminArea = adminArea,
                    countryName = countryName
                )
            }
            COUNTRY -> administrativeUnitDAO.selectGeoLocations(countryName = countryName)
        }.map { it.toGeoLocations() }
    }

    override suspend fun create(geoLocation: GeoLocation, administrativeUnit: AdministrativeUnit) {
        administrativeUnitDAO.insert(
            geoLocation = geoLocation,
            administrativeUnit = administrativeUnit
        )
    }

    override fun retrieve()
        : Flow<List<Pair<AdministrativeUnit, List<CartographicBoundary>>>> {
        return administrativeUnitDAO.selectAdministrativeUnitWithCartographicBoundaries()
            .map { administrativeUnitWithGeoLocations ->
                administrativeUnitWithGeoLocations
                    .toAdministrativeUnitWithCartographicBoundariesList()
            }
    }
}