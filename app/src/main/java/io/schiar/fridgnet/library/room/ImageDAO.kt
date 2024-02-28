package io.schiar.fridgnet.library.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.RewriteQueriesToDropUnusedColumns
import androidx.room.Transaction
import io.schiar.fridgnet.library.room.entity.CoordinateEntity
import io.schiar.fridgnet.library.room.entity.ImageEntity
import io.schiar.fridgnet.library.room.relationentity.ImageWithCoordinate

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