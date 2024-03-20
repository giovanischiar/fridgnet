package io.schiar.fridgnet.model.datasource.local

import io.schiar.fridgnet.model.AdminUnit
import io.schiar.fridgnet.model.datasource.CurrentAdminUnitDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

class CurrentLocalAdminUnitDataSource: CurrentAdminUnitDataSource {
    private val adminUnit = MutableStateFlow<AdminUnit?>(value = null)

    override fun retrieve(): Flow<AdminUnit?> {
        return adminUnit
    }

    override fun update(adminUnit: AdminUnit) {
        this.adminUnit.update { adminUnit }
    }
}