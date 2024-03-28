package io.schiar.fridgnet.model.datasource

import io.schiar.fridgnet.model.AdministrativeLevel
import io.schiar.fridgnet.model.AdministrativeUnit
import io.schiar.fridgnet.model.Region
import kotlinx.coroutines.flow.Flow

interface AdministrativeUnitDataSource {
    fun retrieve(administrativeLevel: AdministrativeLevel): Flow<List<AdministrativeUnit>>
    fun retrieveRegions(): Flow<List<Region>>
    fun retrieveCurrent(): Flow<AdministrativeUnit>
    fun updateCurrentIndex(index: Int)
}