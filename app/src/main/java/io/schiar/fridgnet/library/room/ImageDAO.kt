package io.schiar.fridgnet.library.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.RewriteQueriesToDropUnusedColumns
import androidx.room.Transaction
import io.schiar.fridgnet.library.room.entity.GeoLocationEntity
import io.schiar.fridgnet.library.room.entity.ImageEntity
import io.schiar.fridgnet.library.room.relationentity.ImageWithAdministrativeUnitNameAndGeoLocation
import io.schiar.fridgnet.model.Image
import kotlinx.coroutines.flow.Flow

/**
 * The class that serves as an interface between the app and the database for CRUD operations
 * (Create, Read, Update, Delete) and retrieval of Image data, potentially
 * including related entities.
 */
@Dao
abstract class ImageDAO {
    /**
     * Inserts a new [ImageEntity] object into the database.
     *
     * This method is intended for Room to handle basic insert operations. For insert images,
     * use `insert(image: Image)` instead.
     */
    @Insert
    abstract suspend fun insert(imageEntity: ImageEntity)

    /**
     * Inserts a new [GeoLocationEntity] into the database.
     *
     * This method is intended for Room to handle basic insert operations. For insert images,
     * use `insert(image: Image)` instead.
     */
    @Insert
    abstract suspend fun insert(geoLocationEntity: GeoLocationEntity): Long

    /**
     * Inserts an Image object into the database, including its associated geolocation.
     * This method uses a transaction to ensure data consistency.
     *
     * It first attempts to find an existing ImageEntity with the same geolocation (latitude and
     * longitude) using `selectImageBy`. If a matching ImageEntity is found (**considering potential
     * duplicates**), the insertion is skipped. Otherwise, the geolocation is inserted first, and
     * its generated ID is used to associate the inserted image.
     *
     * @param image the Image object to insert.
     */
    @Transaction
    open suspend fun insert(image: Image) {
        val (_, latitude, longitude) = image.geoLocation
        val imageEntity = selectImageBy(latitude, longitude)
        if (imageEntity == null) {
            val geoLocationID = insert(geoLocationEntity = image.geoLocation.toGeoLocationEntity())
            insert(imageEntity = image.toImageEntity(geoLocationID = geoLocationID))
        }
    }

    /**
     * Retrieves an Image object with its associated GeoLocation and AdministrativeUnitName,
     * or null if no matching image is found.
     *
     * This method uses a transaction and a custom SQL query to perform a JOIN between Image and
     * GeoLocation tables. It searches for images where the GeoLocation.latitude and
     * GeoLocation.longitude match the provided parameters.
     *
     * @param latitude the latitude value to search for.
     * @param longitude the longitude value to search for.
     * @return an Image object with its GeoLocation and AdministrativeUnitName data (can be null if
     * no match is found).
     */
    @Transaction
    @RewriteQueriesToDropUnusedColumns
    @Query(
        "SELECT * " +
        "FROM Image JOIN GeoLocation ON Image.geoLocationID is GeoLocation.id " +
        "WHERE GeoLocation.latitude is :latitude AND " +
              "GeoLocation.longitude is :longitude"
    )
    abstract suspend fun selectImageBy(
        latitude: Double, longitude: Double
    ): ImageWithAdministrativeUnitNameAndGeoLocation?

    /**
     * Retrieves a [Flow] of [List]s containing all [Image] objects with their associated
     * GeoLocation and potentially their AdministrativeUnitName data.
     *
     * This method uses a transaction and a custom SQL query to retrieve all Image data from the
     * database. The emitted Lists within the Flow might contain Image objects with null
     * AdministrativeUnitName if the data is not available.
     *
     * @return a Flow that emits Lists of ImageWithAdministrativeUnitNameAndGeoLocation objects.
     */
    @Transaction
    @RewriteQueriesToDropUnusedColumns
    @Query("SELECT * FROM Image")
    abstract fun selectImagesWithGeoLocationAndAdministrativeUnitName()
        : Flow<List<ImageWithAdministrativeUnitNameAndGeoLocation>>

    /**
     * Delete all Images from Database
     */
    @Query("DELETE FROM Image")
    abstract suspend fun deleteAll()
}