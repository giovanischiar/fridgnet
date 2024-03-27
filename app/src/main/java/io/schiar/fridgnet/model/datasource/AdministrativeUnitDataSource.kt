package io.schiar.fridgnet.model.datasource

import io.schiar.fridgnet.model.AdministrativeLevel
import io.schiar.fridgnet.model.AdministrativeUnit
import kotlinx.coroutines.flow.Flow

interface AdministrativeUnitDataSource {
    fun retrieve(administrativeLevel: AdministrativeLevel): Flow<List<AdministrativeUnit>>
    fun retrieveCurrent(): Flow<AdministrativeUnit>
    fun updateCurrentIndex(index: Int)
}