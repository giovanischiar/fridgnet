package io.schiar.fridgnet.library.room.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * The class representation of the entity 'AdministrativeUnitName' from the database.
 * This entity stores information about the names of administrative units (e.g., city, county,
 * state).
 *
 * @property id           The database ID of the administrative unit name (defaults to 0).
 * @property locality     The name of the first administrative level (e.g., city). Can be null
 * (optional).
 * @property subAdminArea The name of the second administrative level for some countries
 * (e.g., county in USA). Can be null (optional).
 * @property adminArea    The name of a higher administrative level for some countries
 * (e.g., state in USA). Can be null (optional).
 * @property countryName  The name of the country. Can be null (optional).
 */
@Entity(tableName = "AdministrativeUnitName")
data class AdministrativeUnitNameEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val locality: String?,
    val subAdminArea: String?,
    val adminArea: String?,
    val countryName: String?
)
