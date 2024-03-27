package io.schiar.fridgnet.model.datasource.local

import io.schiar.fridgnet.model.AdministrativeUnit
import io.schiar.fridgnet.model.datasource.CurrentAdministrativeUnitDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

class CurrentLocalAdministrativeUnitDataSource @Inject constructor()
    : CurrentAdministrativeUnitDataSource {
    private val administrativeUnit = MutableStateFlow<AdministrativeUnit?>(value = null)

    override fun retrieve(): Flow<AdministrativeUnit?> {
        return administrativeUnit
    }

    override fun update(administrativeUnit: AdministrativeUnit) {
        this.administrativeUnit.update { administrativeUnit }
    }
}