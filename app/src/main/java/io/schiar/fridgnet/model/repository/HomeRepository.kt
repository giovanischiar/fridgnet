package io.schiar.fridgnet.model.repository

import io.schiar.fridgnet.Log
import io.schiar.fridgnet.model.AdministrativeLevel
import io.schiar.fridgnet.model.AdministrativeLevel.CITY
import io.schiar.fridgnet.model.AdministrativeLevel.COUNTRY
import io.schiar.fridgnet.model.AdministrativeLevel.COUNTY
import io.schiar.fridgnet.model.AdministrativeLevel.STATE
import io.schiar.fridgnet.model.AdministrativeUnit
import io.schiar.fridgnet.model.AdministrativeUnitName
import io.schiar.fridgnet.model.CartographicBoundary
import io.schiar.fridgnet.model.GeoLocation
import io.schiar.fridgnet.model.Image
import io.schiar.fridgnet.model.datasource.AdministrativeUnitNameDataSource
import io.schiar.fridgnet.model.datasource.CartographicBoundaryDataSource
import io.schiar.fridgnet.model.datasource.CurrentAdministrativeUnitDataSource
import io.schiar.fridgnet.model.datasource.ImageDataSource
import io.schiar.fridgnet.model.datasource.retriever.AdministrativeUnitNameRetriever
import io.schiar.fridgnet.model.datasource.retriever.CartographicBoundaryRetriever
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import java.util.Collections.synchronizedList as syncListOf
import java.util.Collections.synchronizedMap as syncMapOf
import java.util.Collections.synchronizedSet as syncSetOf

class HomeRepository @Inject constructor(
    private val administrativeUnitNameRetriever: AdministrativeUnitNameRetriever,
    private val administrativeUnitNameDataSource: AdministrativeUnitNameDataSource,
    private val cartographicBoundaryRetriever: CartographicBoundaryRetriever,
    private val cartographicBoundaryDataSource: CartographicBoundaryDataSource,
    private val imageDataSource: ImageDataSource,
    private val currentAdministrativeUnitDataSource : CurrentAdministrativeUnitDataSource,
    private val externalScope: CoroutineScope
) {
    private val administrativeLevels = AdministrativeLevel.entries
    val administrativeLevelsFlow: Flow<List<AdministrativeLevel>> = MutableStateFlow(
        administrativeLevels
    )
    private var currentAdministrativeLevel = CITY
    private val currentAdministrativeLevelStateFlow = MutableStateFlow(currentAdministrativeLevel)
    val currentAdministrativeLevelFlow: Flow<AdministrativeLevel> = run {
        currentAdministrativeLevelStateFlow
    }
    private val administrativeUnitNamesByAdministrativeLevel = syncMapOf(
        administrativeLevels.associateWith { syncListOf(mutableListOf<String>()) }
    )
    private val administrativeUnitByName = syncMapOf(mutableMapOf<String, AdministrativeUnit>())
    private val administrativeUnits: List<AdministrativeUnit> get() {
        return (administrativeUnitNamesByAdministrativeLevel[
            currentAdministrativeLevel
        ] ?: emptyList()).mapNotNull { administrativeUnitByName[it] }
    }
    val administrativeUnitsFlow = merge(
        imageDataSource.retrieveImageWithOptionalAdministrativeUnitName()
            .onEach { (image, optionalAdministrativeUnitName) ->
                if (optionalAdministrativeUnitName != null) {
                    createEachLevelOfAdministrativeUnitWith(image, optionalAdministrativeUnitName)
                    return@onEach
                }
                val geoLocation = image.geoLocation
                launchCoroutineToRetrieveMissingAdministrativeUnitNameFrom(geoLocation)
            },
        administrativeUnitNameDataSource
            .retrieveAdministrativeUnitNameWithExistentCartographicBoundaries()
            .onEach { (administrativeUnitName, existentCartographicBoundaries) ->
                existentCartographicBoundaries.forEach { cartographicBoundary ->
                    createAdministrativeUnitFrom(cartographicBoundary)
                }
                if (existentCartographicBoundaries.size == administrativeLevels.size) return@onEach
                launchCoroutineToRetrieveMissingCartographicBoundariesFrom(
                    administrativeUnitName, existentCartographicBoundaries
                )
            },
        cartographicBoundaryDataSource.retrieve()
            .onEach { cartographicBoundary -> createAdministrativeUnitFrom(cartographicBoundary) },
        currentAdministrativeLevelStateFlow.onEach { currentAdministrativeLevel = it }
    ).map { administrativeUnits }

    private val geoLocationRetrievingAdministrativeUnitNameSet = syncSetOf(
        mutableSetOf<GeoLocation>()
    )
    private val administrativeUnitNameRetrievingCartographicBoundarySet = syncSetOf(
        mutableSetOf<String>()
    )

    fun selectAdministrativeUnitAt(index: Int) {
        val administrativeUnit = administrativeUnits[index]
        log(msg = "AdministrativeUnit at $index is $administrativeUnit")
        currentAdministrativeUnitDataSource.update(administrativeUnit)
    }

    fun changeCurrentAdministrativeLevel(index: Int) {
        currentAdministrativeLevelStateFlow.update { administrativeLevels[index] }
    }

    suspend fun removeAllImages() { imageDataSource.delete() }

    private fun launchCoroutineToRetrieveMissingAdministrativeUnitNameFrom(
        geoLocation: GeoLocation
    ) {
        val administrativeUnitNameFlow = retrieveAdministrativeUnitNameUsing(geoLocation)
            .onEach { administrativeUnitName ->
                administrativeUnitNameDataSource.create(geoLocation, administrativeUnitName)
            }
        externalScope.launch { administrativeUnitNameFlow.collect() }
    }

    private fun launchCoroutineToRetrieveMissingCartographicBoundariesFrom(
        administrativeUnitName: AdministrativeUnitName,
        existentCartographicBoundaries: List<CartographicBoundary>
    ) {
        val cartographicBoundaryFlow = retrieveOnlyNonExistentCartographicBoundariesFrom(
            administrativeUnitName, existentCartographicBoundaries
        ).onEach { cartographyBoundary ->
            createAdministrativeUnitFrom(cartographyBoundary)
            cartographicBoundaryDataSource.create(cartographyBoundary)
        }
        externalScope.launch { cartographicBoundaryFlow.collect() }
    }

    private fun createEachLevelOfAdministrativeUnitWith(
        image: Image, administrativeUnitName: AdministrativeUnitName
    ) {
        val country = createAdministrativeUnit(
            administrativeUnitName, administrativeLevel = COUNTRY, image = image
        )
        val state = createAdministrativeUnit(
            administrativeUnitName, administrativeLevel = STATE, image = image
        )
        val county = createAdministrativeUnit(
            administrativeUnitName, administrativeLevel = COUNTY, image = image
        )
        val city = createAdministrativeUnit(
            administrativeUnitName, administrativeLevel = CITY, image = image
        )
        if (county.subAdministrativeUnits.add(city)) {
            log(
                msg = "Adding the ${city.administrativeLevel} ${city.firstName} " +
                        "to the ${county.administrativeLevel} ${county.firstName}"
            )
        }

        if (state.subAdministrativeUnits.add(county)) {
            log(
                msg ="Adding the ${county.administrativeLevel} ${county.firstName} " +
                        "to the ${state.administrativeLevel} ${state.firstName}"
            )
        }

        if (country.subAdministrativeUnits.add(state)) {
            log(
                msg = "Adding the ${state.administrativeLevel} ${state.firstName} " +
                        "to the ${country.administrativeLevel} ${country.firstName}"
            )
        }
    }

    private fun createAdministrativeUnit(
        administrativeUnitName: AdministrativeUnitName,
        cartographicBoundary: CartographicBoundary? = null,
        administrativeLevel: AdministrativeLevel,
        image: Image? = null
    ): AdministrativeUnit {
        val administrativeLevelWithName = administrativeLevel.with(administrativeUnitName)
        val administrativeUnit = administrativeUnitByName[administrativeLevelWithName]
        return if (administrativeUnit == null) {
            val newAdministrativeUnit = AdministrativeUnit(
                name = administrativeUnitName.toString(administrativeLevel = administrativeLevel),
                administrativeLevel,
                cartographicBoundary,
                images = if (image == null) mutableSetOf() else mutableSetOf(image)
            )
            administrativeUnitByName[administrativeLevelWithName] = newAdministrativeUnit
            administrativeUnitNamesByAdministrativeLevel[administrativeLevel]?.add(
                administrativeLevelWithName
            )
            newAdministrativeUnit
        } else {
            if (image != null) administrativeUnit.images.add(image)
            if (cartographicBoundary != null) {
                administrativeUnit.cartographicBoundary = cartographicBoundary
            }
            administrativeUnit
        }
    }

    private fun createAdministrativeUnitFrom(cartographicBoundary: CartographicBoundary) {
        val (_, administrativeUnitName, _, _, _, administrativeLevel) = cartographicBoundary
        createAdministrativeUnit(administrativeUnitName, cartographicBoundary, administrativeLevel)
    }

    private fun retrieveAdministrativeUnitNameUsing(
        geoLocation: GeoLocation
    ): Flow<AdministrativeUnitName> {
        return if (!geoLocationRetrievingAdministrativeUnitNameSet.contains(geoLocation)) {
            geoLocationRetrievingAdministrativeUnitNameSet.add(geoLocation)
            administrativeUnitNameRetriever.retrieve(geoLocation)
        } else { flowOf(value = null) }.filterNotNull()
    }

    private fun cartographicBoundariesToRetrieve(
        administrativeUnitName: AdministrativeUnitName,
        existentCartographicBoundaries: List<CartographicBoundary>
    ) : List<Pair<AdministrativeLevel, AdministrativeUnitName>> {
        administrativeUnitNameRetrievingCartographicBoundarySet.addAll(
            existentCartographicBoundaries.map { cartographicBoundary ->
                cartographicBoundary.administrationLevelWithName
            }
        )
        return administrativeLevels.mapNotNull { administrativeLevel ->
            val administrativeLevelWithName = administrativeLevel.with(administrativeUnitName)
            val cartographicBoundaryAlreadyBeingRetrieved =
                administrativeUnitNameRetrievingCartographicBoundarySet.contains(
                    administrativeLevelWithName
                )
            if (!cartographicBoundaryAlreadyBeingRetrieved) {
                administrativeUnitNameRetrievingCartographicBoundarySet.add(
                    administrativeLevelWithName
                )
                Pair(administrativeLevel, administrativeUnitName)
            } else { null }
        }
    }

    private fun retrieveOnlyNonExistentCartographicBoundariesFrom(
        administrativeUnitName: AdministrativeUnitName,
        existentCartographicBoundaries: List<CartographicBoundary>
    ): Flow<CartographicBoundary> {
        val administrativeUnitLevelAndAdministrativeUnitNameList = cartographicBoundariesToRetrieve(
            administrativeUnitName, existentCartographicBoundaries
        )

        return cartographicBoundaryRetriever.retrieve(
            administrativeUnitLevelAndAdministrativeUnitNameList
        )
    }

    private fun log(msg: String) {
        val methodName = Thread.currentThread().stackTrace[3].methodName
        Log.d(tag = "HomeRepository.$methodName", msg = msg)
    }
}