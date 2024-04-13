package io.schiar.fridgnet.model.datasource

import io.schiar.fridgnet.model.AdministrativeUnitName
import io.schiar.fridgnet.model.CartographicBoundary
import io.schiar.fridgnet.model.GeoLocation
import kotlinx.coroutines.flow.Flow

/**
 * Interface representing a data source for managing [AdministrativeUnitName] objects. This data
 * source could be local storage, a remote server, or another solution for storing administrative
 * unit names.
 */
interface AdministrativeUnitNameDataSource {
    /**
     * Creates a new [AdministrativeUnitName] in the data source, associating it with the provided
     * [geoLocation]. The reason for the [geoLocation] parameter might be to associate the name with
     * a specific geographic location for future lookups or other purposes.
     *
     * @param geoLocation            the geo location associated with the administrative unit name.
     * @param administrativeUnitName the [AdministrativeUnitName] object to be created.
     */
    suspend fun create(geoLocation: GeoLocation, administrativeUnitName: AdministrativeUnitName)

    /**
     * Retrieves a stream (Flow) of pairs containing an [AdministrativeUnitName] object and a list
     * of associated [CartographicBoundary] objects. This Flow will emit updates whenever a new
     * administrative unit name is created or an existing one is updated.
     *
     * @return a Flow of pairs: [AdministrativeUnitName] and a list of [CartographicBoundary]
     *         objects.
     */
    fun retrieveAdministrativeUnitNameWithExistentCartographicBoundaries()
        : Flow<Pair<AdministrativeUnitName, List<CartographicBoundary>>>
}