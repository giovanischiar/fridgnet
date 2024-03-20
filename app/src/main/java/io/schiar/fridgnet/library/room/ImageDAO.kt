package io.schiar.fridgnet.library.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.RewriteQueriesToDropUnusedColumns
import androidx.room.Transaction
import io.schiar.fridgnet.library.room.entity.GeoLocationEntity
import io.schiar.fridgnet.library.room.entity.ImageEntity
import io.schiar.fridgnet.library.room.relationentity.ImageWithAdministrativeUnitAndGeoLocation
import io.schiar.fridgnet.model.Image
import kotlinx.coroutines.flow.Flow

@Dao
abstract class ImageDAO {
    @Insert
    abstract suspend fun insert(imageEntity: ImageEntity)

    @Insert
    abstract suspend fun insert(geoLocationEntity: GeoLocationEntity): Long

    @Transaction
    open suspend fun insert(image: Image) {
        val (_, latitude, longitude) = image.geoLocation
        val imageEntity = selectImageBy(latitude, longitude)
        if (imageEntity == null) {
            val geoLocationID = insert(geoLocationEntity = image.geoLocation.toGeoLocationEntity())
            insert(imageEntity = image.toImageEntity(geoLocationID = geoLocationID))
        }
    }

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
    ): ImageWithAdministrativeUnitAndGeoLocation?

    @Transaction
    @RewriteQueriesToDropUnusedColumns
    @Query("SELECT * FROM Image")
    abstract fun selectImagesWithGeoLocationAndAdministrativeUnit()
        : Flow<List<ImageWithAdministrativeUnitAndGeoLocation>>

    @Transaction
    @RewriteQueriesToDropUnusedColumns
    @Query("SELECT * FROM Image")
    abstract fun selectImagesWithAdministrativeUnitAndGeoLocation()
        : Flow<List<ImageWithAdministrativeUnitAndGeoLocation>>

    @Query("DELETE FROM Image")
    abstract suspend fun deleteAll()
}