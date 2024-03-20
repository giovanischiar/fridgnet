package io.schiar.fridgnet.model.datasource

import io.schiar.fridgnet.model.AdminUnit
import kotlinx.coroutines.flow.Flow

interface CurrentAdminUnitDataSource {
    fun retrieve(): Flow<AdminUnit?>
    fun update(adminUnit: AdminUnit)
}