package io.schiar.fridgnet.model.datasource.room

import androidx.room.*
import io.schiar.fridgnet.model.datasource.room.entity.CoordinateEntity
import io.schiar.fridgnet.model.datasource.room.entity.ImageEntity
import io.schiar.fridgnet.model.datasource.room.relationentity.ImageWithCoordinate

@Dao
interface ImageDAO {
    @Insert
    fun insert(imageEntity: ImageEntity)

    @Insert
    fun insert(coordinateEntity: CoordinateEntity): Long

    @Transaction
    @RewriteQueriesToDropUnusedColumns
    @Query("SELECT * FROM Image WHERE Image.uri is :uri")
    fun selectImageBy(uri: String): ImageWithCoordinate?

    @Transaction
    @RewriteQueriesToDropUnusedColumns
    @Query(
        "SELECT * FROM Image JOIN Coordinate ON Image.coordinateID is Coordinate.id " +
        "WHERE Coordinate.latitude is :latitude AND Coordinate.longitude is :longitude"
    )
    fun selectImageBy(latitude: Double, longitude: Double): ImageWithCoordinate?

    @Transaction
    @RewriteQueriesToDropUnusedColumns
    @Query("SELECT * FROM Image")
    fun selectImagesWithCoordinate(): List<ImageWithCoordinate>

    @Query("DELETE FROM Image")
    fun deleteAll()
}