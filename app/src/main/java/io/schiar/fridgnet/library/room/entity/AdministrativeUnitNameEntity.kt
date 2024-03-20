package io.schiar.fridgnet.library.room.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "AdministrativeUnitName")
data class AdministrativeUnitNameEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val locality: String?,
    val subAdminArea: String?,
    val adminArea: String?,
    val countryName: String?
)
