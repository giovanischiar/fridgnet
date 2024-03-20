package io.schiar.fridgnet.model.datasource

import io.schiar.fridgnet.model.AdministrativeUnit
import kotlinx.coroutines.flow.Flow

interface CurrentAdministrativeUnitDataSource {
    fun retrieve(): Flow<AdministrativeUnit?>
    fun update(administrativeUnit: AdministrativeUnit)
}