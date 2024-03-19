package io.schiar.fridgnet.model.repository

import io.schiar.fridgnet.Log
import io.schiar.fridgnet.model.AdministrativeUnit
import io.schiar.fridgnet.model.AdministrativeUnitLocationsGeoLocations
import io.schiar.fridgnet.model.AdministrativeLevel
import io.schiar.fridgnet.model.AdministrativeLevel.CITY
import io.schiar.fridgnet.model.GeoLocation
import io.schiar.fridgnet.model.ImageAdministrativeUnit
import io.schiar.fridgnet.model.Location
import io.schiar.fridgnet.model.LocationGeoLocation
import io.schiar.fridgnet.model.datasource.AdministrativeUnitDataSource
import io.schiar.fridgnet.model.datasource.ImageDataSource
import io.schiar.fridgnet.model.datasource.LocationDataSource
import io.schiar.fridgnet.model.datasource.local.CurrentLocationGeoLocationDataSource
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
    private val locationDataSource: LocationDataSource,
    private val imageDataSource: ImageDataSource,
    private val currentLocationGeoLocationDataSource : CurrentLocationGeoLocationDataSource,
    private val externalScope: CoroutineScope
) {
    private var _currentAdministrativeLevel = CITY
    private val _administrativeLevels = AdministrativeLevel.entries
    private val _currentAdministrativeLevelStateFlow = MutableStateFlow(_currentAdministrativeLevel)
    private var _currentLocationGeoLocations: List<LocationGeoLocation> = emptyList()

    val administrativeLevels: Flow<List<AdministrativeLevel>> = MutableStateFlow(_administrativeLevels)
    val currentAdministrativeLevel: Flow<AdministrativeLevel> = _currentAdministrativeLevelStateFlow

    private val geoLocationRetrievingAdministrativeUnitSet = syncSetOf(mutableSetOf<GeoLocation>())
    private val administrativeUnitRetrievingLocationSet = syncSetOf(mutableSetOf<String>())

    private val administrativeLevelAdministrativeUnitNameLocationGeoLocation = run {
        syncMapOf(_administrativeLevels.associateWith { mutableMapOf<String, LocationGeoLocation>() })
    }

    val locationGeoLocations = merge(
        imageDataSource.retrieveWithAdministrativeUnit().onEach { it.forEach(::onEachImageAdministrativeUnit) },
        administrativeUnitDataSource.retrieve().onEach { it.forEach(::onEachAdministrativeUnitLocationsGeoLocations) },
        locationDataSource.retrieve().onEach { it.forEach(::onEachLocation) },
        _currentAdministrativeLevelStateFlow.onEach { _currentAdministrativeLevel = it }
    ).map {
        _currentLocationGeoLocations = administrativeLevelAdministrativeUnitNameLocationGeoLocation[
            _currentAdministrativeLevel
        ]?.values?.toList() ?: emptyList()
        _currentLocationGeoLocations
    }

    private fun onEachImageAdministrativeUnit(imageAdministrativeUnit: ImageAdministrativeUnit) {
        val (image, administrativeUnitRetrieved) = imageAdministrativeUnit
        val geoLocation = image.geoLocation
        if (administrativeUnitRetrieved != null) {
            assignNewLocationGeoLocation(
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
        administrativeUnitLocationsGeoLocations: AdministrativeUnitLocationsGeoLocations
    ) {
        val (administrativeUnit, geoLocations, administrativeLevelLocation) = administrativeUnitLocationsGeoLocations
        val locationsRetrieved = administrativeLevelLocation.values
        assignNewLocationGeoLocation(
            administrativeLevel = CITY,
            administrativeUnitName = administrativeUnit.name(),
            location = administrativeLevelLocation[CITY],
            initialGeoLocation = geoLocations[0]
        )
        retrieveLocationsForAdministrativeUnit(administrativeUnit = administrativeUnit, locationsFromAdministrativeUnit = locationsRetrieved)
    }

    private fun onEachLocation(location: Location) {
        assignNewLocationGeoLocation(
            administrativeLevel = location.administrativeLevel,
            administrativeUnitName = location.administrativeUnitName(),
            location = location,
            initialGeoLocation = location.boundingBox.center()
        )
    }

    fun selectLocationGeoLocationAt(index: Int) {
        val locationGeoLocation = _currentLocationGeoLocations[index]
        log(
            method = "selectLocationGeoLocationAt",
            msg = "LocationGeoLocation at $index is $locationGeoLocation"
        )
        currentLocationGeoLocationDataSource.update(locationGeoLocation = locationGeoLocation)
    }

    fun changeCurrentAdministrativeLevel(index: Int) {
        _currentAdministrativeLevelStateFlow.update { _administrativeLevels[index] }
    }

    suspend fun removeAllImages() { imageDataSource.delete() }

    private fun retrieveAdministrativeUnitForGeoLocation(
        geoLocation: GeoLocation, administrativeUnitFromGeoLocation: AdministrativeUnit?
    ) {
        if (administrativeUnitFromGeoLocation != null) geoLocationRetrievingAdministrativeUnitSet.add(geoLocation)
        val administrativeUnitNotBeingRetrieved = !geoLocationRetrievingAdministrativeUnitSet.contains(geoLocation)
        if (administrativeUnitNotBeingRetrieved) {
            geoLocationRetrievingAdministrativeUnitSet.add(element = geoLocation)
            log(method = "retrieveAdministrativeUnitForGeoLocation", msg = "Retrieve AdministrativeUnit for $geoLocation")
            externalScope.launch { administrativeUnitDataSource.retrieveAdministrativeUnitFor(geoLocation = geoLocation) }
        }
    }

    private fun retrieveLocationsForAdministrativeUnit(
        administrativeUnit: AdministrativeUnit, locationsFromAdministrativeUnit: Collection<Location>
    ) {
        administrativeUnitRetrievingLocationSet.addAll(locationsFromAdministrativeUnit.map { it.administrativeUnitName() })

        val administrativeLevelsWithNonRetrievedLocation = _administrativeLevels.filter {
                administrativeLevel -> run {
                val administrativeUnitName = administrativeUnit.name(administrativeLevel = administrativeLevel)
                !administrativeUnitRetrievingLocationSet.contains(administrativeUnitName)
            }
        }

        for (administrativeLevel in administrativeLevelsWithNonRetrievedLocation) {
            val administrativeUnitName = administrativeUnit.name(administrativeLevel = administrativeLevel)
            administrativeUnitRetrievingLocationSet.add(administrativeUnitName)
            externalScope.launch {
                log(
                    method = "retrieveLocationForAdministrativeUnit",
                    msg = "Retrieve Location for $administrativeUnitName"
                )
                locationDataSource.retrieveLocationFor(
                    administrativeUnit = administrativeUnit,
                    administrativeLevel = administrativeLevel
                )
            }
        }
    }

    private fun assignNewLocationGeoLocation(
        administrativeLevel: AdministrativeLevel,
        administrativeUnitName: String,
        location: Location? = null,
        initialGeoLocation: GeoLocation
    ) {
        val locationGeoLocation = (administrativeLevelAdministrativeUnitNameLocationGeoLocation[
            administrativeLevel
        ] ?: return)[administrativeUnitName]
        val needToAssignNewLocationGeoLocation = locationGeoLocation == null ||
                (locationGeoLocation.location == null && location != null)

        if (needToAssignNewLocationGeoLocation) {
            val newLocationGeoLocation = LocationGeoLocation(
                location = location,
                initialGeoLocation = initialGeoLocation
            )
            log(
                method = "assignNewLocationGeoLocation",
                msg = "Assign $newLocationGeoLocation " +
                        "to administrativeLevelAdministrativeUnitNameLocationGeoLocation[" +
                        "$administrativeLevel" +
                       "][$administrativeUnitName]"
            )
            administrativeLevelAdministrativeUnitNameLocationGeoLocation[
                administrativeLevel
            ]?.set(administrativeUnitName, newLocationGeoLocation)
        }
    }

    private fun log(method: String, msg: String) {
        Log.d(tag = "HomeRepository.$method", msg = msg)
    }
}