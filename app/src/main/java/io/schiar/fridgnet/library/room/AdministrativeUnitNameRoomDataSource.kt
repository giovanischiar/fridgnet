package io.schiar.fridgnet.library.room

import io.schiar.fridgnet.model.AdministrativeLevel
import io.schiar.fridgnet.model.AdministrativeLevel.CITY
import io.schiar.fridgnet.model.AdministrativeLevel.COUNTRY
import io.schiar.fridgnet.model.AdministrativeLevel.COUNTY
import io.schiar.fridgnet.model.AdministrativeLevel.STATE
import io.schiar.fridgnet.model.AdministrativeUnitName
import io.schiar.fridgnet.model.CartographicBoundary
import io.schiar.fridgnet.model.GeoLocation
import io.schiar.fridgnet.model.datasource.AdministrativeUnitNameDataSource
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

class AdministrativeUnitNameRoomDataSource(
    private val administrativeUnitNameDAO: AdministrativeUnitNameDAO
) : AdministrativeUnitNameDataSource {
    private val administrativeUnitNameIDS = mutableSetOf<Long>()

    override fun retrieveGeoLocations(
        administrativeUnitName: AdministrativeUnitName, administrativeLevel: AdministrativeLevel
    ): Flow<List<GeoLocation>> {
        val (_, locality, subAdminArea, adminArea, countryName) = administrativeUnitName
        return when(administrativeLevel) {
            CITY -> administrativeUnitNameDAO.selectGeoLocations(
                locality = locality,
                subAdminArea = subAdminArea,
                adminArea = adminArea,
                countryName = countryName
            )
            COUNTY -> administrativeUnitNameDAO.selectGeoLocations(
                subAdminArea = subAdminArea, adminArea = adminArea, countryName = countryName
            )
            STATE -> {
                administrativeUnitNameDAO.selectGeoLocations(
                    adminArea = adminArea,
                    countryName = countryName
                )
            }
            COUNTRY -> administrativeUnitNameDAO.selectGeoLocations(countryName = countryName)
        }.map { it.toGeoLocations() }
    }

    override suspend fun create(geoLocation: GeoLocation, administrativeUnitName: AdministrativeUnitName) {
        administrativeUnitNameDAO.insert(
            geoLocation = geoLocation,
            administrativeUnitName = administrativeUnitName
        )
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun retrieve(): Flow<Pair<AdministrativeUnitName, List<CartographicBoundary>>> {
        return administrativeUnitNameDAO.selectAdministrativeUnitNameWithCartographicBoundaries()
            .flatMapLatest { administrativeUnitNameWithCartographicBoundariesList ->
                flow {
                    for (
                        administrativeUnitNameWithCartographicBoundaries in
                        administrativeUnitNameWithCartographicBoundariesList
                    ) {
                        val administrativeUnitNameID
                            = administrativeUnitNameWithCartographicBoundaries
                            .administrativeUnitNameEntity
                            .id
                        if (administrativeUnitNameIDS.add(element = administrativeUnitNameID)) {
                            emit(
                                administrativeUnitNameWithCartographicBoundaries
                                    .toAdministrativeUnitNameAndCartographicBoundaries()
                            )
                        }
                    }
                }
            }
    }
}