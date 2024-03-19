package io.schiar.fridgnet.model.repository

import io.schiar.fridgnet.Log
import io.schiar.fridgnet.model.AdministrativeLevel
import io.schiar.fridgnet.model.AdministrativeLevel.CITY
import io.schiar.fridgnet.model.AdministrativeUnit
import io.schiar.fridgnet.model.AdministrativeUnitCartographicBoundariesGeoLocations
import io.schiar.fridgnet.model.CartographicBoundary
import io.schiar.fridgnet.model.CartographicBoundaryGeoLocation
import io.schiar.fridgnet.model.GeoLocation
import io.schiar.fridgnet.model.ImageAdministrativeUnit
import io.schiar.fridgnet.model.datasource.AdministrativeUnitDataSource
import io.schiar.fridgnet.model.datasource.CartographicBoundaryDataSource
import io.schiar.fridgnet.model.datasource.ImageDataSource
import io.schiar.fridgnet.model.datasource.local.CurrentCartographicBoundaryGeoLocationDataSource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Collections.synchronizedMap as syncMapOf
import java.util.Collections.synchronizedSet as syncSetOf

class HomeRepository(
    private val administrativeUnitDataSource: AdministrativeUnitDataSource,
    private val cartographicBoundaryDataSource: CartographicBoundaryDataSource,
    private val imageDataSource: ImageDataSource,
    private val currentCartographicBoundaryGeoLocationsDataSource :
        CurrentCartographicBoundaryGeoLocationDataSource,
    private val externalScope: CoroutineScope
) {
    private var _currentAdministrativeLevel = CITY
    private val _administrativeLevels = AdministrativeLevel.entries
    private val _currentAdministrativeLevelStateFlow = MutableStateFlow(_currentAdministrativeLevel)
    private var _currentCartographicBoundaryGeoLocations: List<CartographicBoundaryGeoLocation>
        = emptyList()

    val administrativeLevels: Flow<List<AdministrativeLevel>>
        = MutableStateFlow(_administrativeLevels)
    val currentAdministrativeLevel: Flow<AdministrativeLevel>
        = _currentAdministrativeLevelStateFlow

    private val geoLocationRetrievingAdministrativeUnitSet = syncSetOf(mutableSetOf<GeoLocation>())
    private val administrativeUnitRetrievingCartographicBoundarySet
        = syncSetOf(mutableSetOf<String>())

    private val administrativeLevelAdministrativeUnitNameCartographicBoundaryGeoLocation = run {
        syncMapOf(_administrativeLevels.associateWith {
            mutableMapOf<String, CartographicBoundaryGeoLocation>()
        })
    }

    val cartographicBoundaryGeoLocations = merge(
        imageDataSource.retrieveWithAdministrativeUnit().onEach { imageAdministrativeUnits ->
            imageAdministrativeUnits.forEach(::onEachImageAdministrativeUnit)
        },
        administrativeUnitDataSource.retrieve().onEach { administrativeUnitLocationsGeoLocations ->
            administrativeUnitLocationsGeoLocations.forEach(
                ::onEachAdministrativeUnitLocationsGeoLocations
            )
        },
        cartographicBoundaryDataSource.retrieve().onEach { cartographicBoundaries ->
            cartographicBoundaries.forEach(::onEachCartographicBoundary)
        },
        _currentAdministrativeLevelStateFlow.onEach { _currentAdministrativeLevel = it }
    ).map {
        _currentCartographicBoundaryGeoLocations =
            administrativeLevelAdministrativeUnitNameCartographicBoundaryGeoLocation[
            _currentAdministrativeLevel
        ]?.values?.toList() ?: emptyList()
        _currentCartographicBoundaryGeoLocations
    }

    private fun onEachImageAdministrativeUnit(imageAdministrativeUnit: ImageAdministrativeUnit) {
        val (image, administrativeUnitRetrieved) = imageAdministrativeUnit
        val geoLocation = image.geoLocation
        if (administrativeUnitRetrieved != null) {
            assignNewCartographicBoundaryGeoLocation(
                administrativeLevel = CITY,
                administrativeUnitName = administrativeUnitRetrieved.name(),
                initialGeoLocation = geoLocation
            )
        }
        retrieveAdministrativeUnitForGeoLocation(
            geoLocation = geoLocation,
            administrativeUnitFromGeoLocation = administrativeUnitRetrieved
        )
    }

    private fun onEachAdministrativeUnitLocationsGeoLocations(
        administrativeUnitLocationsGeoLocations:
            AdministrativeUnitCartographicBoundariesGeoLocations
    ) {
        val (
            administrativeUnit,
            geoLocations,
            administrativeLevelCartographicBoundary
        ) = administrativeUnitLocationsGeoLocations
        val cartographicBoundariesRetrieved = administrativeLevelCartographicBoundary.values
        assignNewCartographicBoundaryGeoLocation(
            administrativeLevel = CITY,
            administrativeUnitName = administrativeUnit.name(),
            cartographicBoundary = administrativeLevelCartographicBoundary[CITY],
            initialGeoLocation = geoLocations[0]
        )
        retrieveCartographicBoundariesForAdministrativeUnit(
            administrativeUnit = administrativeUnit,
            cartographicBoundariesFromAdministrativeUnit = cartographicBoundariesRetrieved
        )
    }

    private fun onEachCartographicBoundary(cartographicBoundary: CartographicBoundary) {
        assignNewCartographicBoundaryGeoLocation(
            administrativeLevel = cartographicBoundary.administrativeLevel,
            administrativeUnitName = cartographicBoundary.administrativeUnitName(),
            cartographicBoundary = cartographicBoundary,
            initialGeoLocation = cartographicBoundary.boundingBox.center()
        )
    }

    fun selectCartographicBoundaryGeoLocationAt(index: Int) {
        val cartographicBoundaryGeoLocation = _currentCartographicBoundaryGeoLocations[index]
        log(
            method = "selectCartographicBoundaryGeoLocationAt",
            msg = "CartographicBoundaryGeoLocation at $index is $cartographicBoundaryGeoLocation"
        )
        currentCartographicBoundaryGeoLocationsDataSource.update(
            cartographicBoundaryGeoLocation = cartographicBoundaryGeoLocation
        )
    }

    fun changeCurrentAdministrativeLevel(index: Int) {
        _currentAdministrativeLevelStateFlow.update { _administrativeLevels[index] }
    }

    suspend fun removeAllImages() { imageDataSource.delete() }

    private fun retrieveAdministrativeUnitForGeoLocation(
        geoLocation: GeoLocation, administrativeUnitFromGeoLocation: AdministrativeUnit?
    ) {
        if (administrativeUnitFromGeoLocation != null) {
            geoLocationRetrievingAdministrativeUnitSet.add(geoLocation)
        }
        val administrativeUnitNotBeingRetrieved =
            !geoLocationRetrievingAdministrativeUnitSet.contains(geoLocation)
        if (administrativeUnitNotBeingRetrieved) {
            geoLocationRetrievingAdministrativeUnitSet.add(element = geoLocation)
            log(
                method = "retrieveAdministrativeUnitForGeoLocation",
                msg = "Retrieve AdministrativeUnit for $geoLocation"
            )
            externalScope.launch {
                administrativeUnitDataSource.retrieveAdministrativeUnitFor(
                    geoLocation = geoLocation
                )
            }
        }
    }

    private fun retrieveCartographicBoundariesForAdministrativeUnit(
        administrativeUnit: AdministrativeUnit,
        cartographicBoundariesFromAdministrativeUnit: Collection<CartographicBoundary>
    ) {
        administrativeUnitRetrievingCartographicBoundarySet.addAll(
            cartographicBoundariesFromAdministrativeUnit.map { it.administrativeUnitName() }
        )

        val administrativeLevelsWithNonRetrievedLocation = _administrativeLevels.filter {
                administrativeLevel -> run {
                val administrativeUnitName = administrativeUnit.name(
                    administrativeLevel = administrativeLevel
                )
                !administrativeUnitRetrievingCartographicBoundarySet.contains(
                    administrativeUnitName
                )
            }
        }

        for (administrativeLevel in administrativeLevelsWithNonRetrievedLocation) {
            val administrativeUnitName = administrativeUnit.name(
                administrativeLevel = administrativeLevel
            )
            administrativeUnitRetrievingCartographicBoundarySet.add(administrativeUnitName)
            externalScope.launch {
                log(
                    method = "retrieveCartographicBoundariesForAdministrativeUnit",
                    msg = "Retrieve Cartographic Boundary for $administrativeUnitName"
                )
                cartographicBoundaryDataSource.retrieveLocationFor(
                    administrativeUnit = administrativeUnit,
                    administrativeLevel = administrativeLevel
                )
            }
        }
    }

    private fun assignNewCartographicBoundaryGeoLocation(
        administrativeLevel: AdministrativeLevel,
        administrativeUnitName: String,
        cartographicBoundary: CartographicBoundary? = null,
        initialGeoLocation: GeoLocation
    ) {
        val cartographicBoundaryGeoLocation = (
                administrativeLevelAdministrativeUnitNameCartographicBoundaryGeoLocation[
            administrativeLevel
        ] ?: return)[administrativeUnitName]
        val needToAssignNewCartographicBoundaryGeoLocation
            = cartographicBoundaryGeoLocation == null ||
                (cartographicBoundaryGeoLocation.cartographicBoundary == null &&
                        cartographicBoundary != null)

        if (needToAssignNewCartographicBoundaryGeoLocation) {
            val newCartographicBoundaryGeoLocation = CartographicBoundaryGeoLocation(
                cartographicBoundary = cartographicBoundary,
                initialGeoLocation = initialGeoLocation
            )
            log(
                method = "assignNewCartographicBoundaryGeoLocation",
                msg = "Assign $newCartographicBoundaryGeoLocation " +
                        "to " +
                        "administrativeLevelAdministrativeUnitNameCartographicBoundaryGeoLocation" +
                        "[$administrativeLevel][$administrativeUnitName]"
            )
            administrativeLevelAdministrativeUnitNameCartographicBoundaryGeoLocation[
                administrativeLevel
            ]?.set(administrativeUnitName, newCartographicBoundaryGeoLocation)
        }
    }

    private fun log(method: String, msg: String) {
        Log.d(tag = "HomeRepository.$method", msg = msg)
    }
}