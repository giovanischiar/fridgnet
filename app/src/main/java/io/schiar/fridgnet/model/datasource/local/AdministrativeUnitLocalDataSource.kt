package io.schiar.fridgnet.model.datasource.local

import io.schiar.fridgnet.Log
import io.schiar.fridgnet.model.AdministrativeLevel
import io.schiar.fridgnet.model.AdministrativeLevel.CITY
import io.schiar.fridgnet.model.AdministrativeLevel.COUNTRY
import io.schiar.fridgnet.model.AdministrativeLevel.COUNTY
import io.schiar.fridgnet.model.AdministrativeLevel.STATE
import io.schiar.fridgnet.model.AdministrativeUnit
import io.schiar.fridgnet.model.AdministrativeUnitName
import io.schiar.fridgnet.model.BoundingBox
import io.schiar.fridgnet.model.CartographicBoundary
import io.schiar.fridgnet.model.GeoLocation
import io.schiar.fridgnet.model.Image
import io.schiar.fridgnet.model.Region
import io.schiar.fridgnet.model.datasource.AdministrativeUnitDataSource
import io.schiar.fridgnet.model.datasource.AdministrativeUnitNameDataSource
import io.schiar.fridgnet.model.datasource.CartographicBoundaryDataSource
import io.schiar.fridgnet.model.datasource.ImageDataSource
import io.schiar.fridgnet.model.datasource.retriever.AdministrativeUnitNameRetriever
import io.schiar.fridgnet.model.datasource.retriever.CartographicBoundaryRetriever
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
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

/**
 * Responsible for interacting with retrievers and data sources of administrative unit names and
 * cartographic boundaries. It creates new administrative unit names and cartographic boundaries
 * each time a new image is added.
 */
class AdministrativeUnitLocalDataSource @Inject constructor(
    private val administrativeUnitNameRetriever: AdministrativeUnitNameRetriever,
    private val administrativeUnitNameDataSource: AdministrativeUnitNameDataSource,
    private val cartographicBoundaryRetriever: CartographicBoundaryRetriever,
    private val cartographicBoundaryDataSource: CartographicBoundaryDataSource,
    private val externalScope: CoroutineScope,
    imageDataSource: ImageDataSource
): AdministrativeUnitDataSource {
    private var lastAdministrativeUnitHashCode = -1
    private val currentAdministrativeUnitIndexFlow = MutableStateFlow(value = -1)
    private val geoLocationRetrievingAdministrativeUnitNameSet = syncSetOf(
        mutableSetOf<GeoLocation>()
    )
    private val administrativeUnitNameRetrievingCartographicBoundarySet = syncSetOf(
        mutableSetOf<String>()
    )
    private val currentAdministrativeLevelStateFlow = MutableStateFlow(CITY)
    private val administrativeLevels = AdministrativeLevel.entries
    private val administrativeUnitNamesByAdministrativeLevel = syncMapOf(
        administrativeLevels.associateWith { syncListOf(mutableListOf<String>()) }
    )
    private val administrativeUnitByName = syncMapOf(mutableMapOf<String, AdministrativeUnit>())

    private val administrativeUnitsFlow = merge(
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
    )

    /**
     * The [administrativeUnitsFlow] is triggered in many ways:
     *
     * - When a new [Image] is created: It can be triggered when an [Image] is created for the first
     *   time, so there is no [AdministrativeUnitName]. A coroutine is launched to create the
     *   [AdministrativeUnitName] using the coordinates extracted from the [Image]. If the [Image]
     *   has an [AdministrativeUnitName], that means it was already created before. This is when the
     *   first [AdministrativeUnit]s are created. Initially, they are created without a
     *   [CartographicBoundary], so the user will see a map without any shapes drawn, but they will
     *   see a map focused on the [Image] where it was drawn. Each new [AdministrativeUnit] is
     *   created based on the information of the [AdministrativeUnitName]. If the
     *   [AdministrativeUnit] was already created, the new image is simply added to the
     *   [AdministrativeUnit] and its outer levels.
     *
     * - When a new [AdministrativeUnitName] is created: Each [AdministrativeUnitName] is received
     *   by the [AdministrativeUnitNameDataSource] along with the list of up to 4
     *   [CartographicBoundary]s, one for each [AdministrativeLevel]. The method
     *   [cartographicBoundariesToRetrieve] filters all missing cartographic boundaries for each
     *   [AdministrativeLevel], and the method [retrieveOnlyNonExistentCartographicBoundariesFrom]
     *   uses the [cartographicBoundaryRetriever] to retrieve all missing [CartographicBoundary]s.
     *
     * - When a new [CartographicBoundary] is created: The method feeds all existing
     *   [AdministrativeUnit]s with their missing [CartographicBoundary]s so the user can now see
     *   the outlines of the [AdministrativeUnit]s inside the map on the screen.
     *
     * @return a [Flow] of [List] of [AdministrativeUnit] from all [AdministrativeLevel]s
     */
    override fun retrieve(): Flow<List<AdministrativeUnit>> {
        return administrativeUnitsFlow.map {
            administrativeLevels.flatMap { administrativeUnitsFrom(it) }
        }
    }

    /**
     * @see retrieve
     *
     * @param administrativeLevel The administrative level used to filter.
     * @return                    A [Flow] of [List] of [AdministrativeUnit] from a specific
     * [AdministrativeLevel].
     */
    override fun retrieve(
        administrativeLevel: AdministrativeLevel
    ): Flow<List<AdministrativeUnit>> {
        currentAdministrativeLevelStateFlow.update { administrativeLevel }
        return merge(administrativeUnitsFlow, currentAdministrativeLevelStateFlow)
            .map { administrativeUnitsFrom(administrativeLevel) }
    }

    /**
     * @param boundingBox The bounding box used to filter.
     * @return            The active regions within a [BoundingBox].
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    override fun retrieveActiveRegionsWithin(boundingBox: BoundingBox): Flow<List<Region>> {
        return merge(flowOf(boundingBox), administrativeUnitsFlow).flatMapLatest {
            activeRegionsFlowWithin(boundingBox)
        }
    }

    private fun activeRegionsFlowWithin(boundingBox: BoundingBox): Flow<List<Region>> = flow {
        val administrativeUnitsFromEveryLevel = administrativeLevels.flatMap {
            administrativeLevel -> administrativeUnitsFrom(administrativeLevel)
        }
        val activeRegionsWithinBoundingBox = administrativeUnitsFromEveryLevel
            .flatMap { administrativeUnit ->
                administrativeUnit.activeCartographicBoundaryRegionsWithin(boundingBox)
            }
        emit(activeRegionsWithinBoundingBox)
    }

    /**
     * Merge the [currentAdministrativeUnitIndexFlow] with [administrativeUnitsFlow] to receive the
     * newly updated flow of the current [AdministrativeUnit]. Both flows are necessary because
     * with the [currentAdministrativeUnitIndexFlow], it's possible to get the currently selected
     * [AdministrativeUnit] when the user selects one on the screen.
     * And the [administrativeUnitsFlow] keeps sending changes that happen on that
     * [AdministrativeUnit] in real-time while the user is on the administrative unit screen.
     * It's guaranteed that the [Flow] only emits the object when there's a new change in it,
     * for example, when a new [Image] is added. This [Flow] always emits the same instance of
     * [AdministrativeUnit] for each index repeatedly. Therefore, you can't compare objects if they
     * are the same instance. To verify if the object really changed its contents, its hash code is
     * annotated. So, the next time this same object tries to emit, the hash code is compared to see
     * if there was really a change, and it is emitted again only if its contents changed.
     *
     * @return A [Flow] of [AdministrativeUnit], representing the current one that the user is
     * seeing on the screen.
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    override fun retrieveCurrent(): Flow<AdministrativeUnit> {
        return merge(
            currentAdministrativeUnitIndexFlow, administrativeUnitsFlow
        ).flatMapLatest { flowOfCurrentAdministrativeUnit() }
    }

    private fun flowOfCurrentAdministrativeUnit(): Flow<AdministrativeUnit> = flow {
        val currentAdministrativeLevel = currentAdministrativeLevelStateFlow.value
        val currentAdministrativeUnitIndex = currentAdministrativeUnitIndexFlow.value
        val currentAdministrativeUnit = administrativeUnitsFrom(currentAdministrativeLevel)
            .getOrNull(currentAdministrativeUnitIndex) ?: return@flow
        if (lastAdministrativeUnitHashCode != currentAdministrativeUnit.hashCode()) {
            lastAdministrativeUnitHashCode = currentAdministrativeUnit.hashCode()
            emit(currentAdministrativeUnit)
        }
    }

    /**
     * Update the current index selected by the user. This [lastAdministrativeUnitHashCode] property
     * is used to compare if the contents of the current [AdministrativeUnit] really changed.
     * If the user goes back to the administrative units screen and selects the same one again,
     * it won't emit it again. To solve that problem, the [lastAdministrativeUnitHashCode] is reset
     * each time this method is called.
     *
     * @param index The new index value of the [AdministrativeUnit] the user selected on the screen.
     */
    override fun updateCurrentIndex(index: Int) {
        lastAdministrativeUnitHashCode = -1
        currentAdministrativeUnitIndexFlow.update { index }
    }

    private fun administrativeUnitsFrom(
        administrativeLevel: AdministrativeLevel
    ): List<AdministrativeUnit> {
        return (administrativeUnitNamesByAdministrativeLevel[
            administrativeLevel
        ] ?: emptyList()).mapNotNull { administrativeUnitByName[it] }
    }

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
        Log.d(tag = "AdministrativeUnitLocalDataSource.$methodName", msg = msg)
    }
}