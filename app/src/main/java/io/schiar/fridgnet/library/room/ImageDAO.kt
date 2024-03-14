package io.schiar.fridgnet.library.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.RewriteQueriesToDropUnusedColumns
import androidx.room.Transaction
import io.schiar.fridgnet.library.room.entity.CoordinateEntity
import io.schiar.fridgnet.library.room.entity.ImageEntity
import io.schiar.fridgnet.library.room.relationentity.ImageWithCoordinate
import io.schiar.fridgnet.model.Image
import kotlinx.coroutines.flow.Flow

@Dao
abstract class ImageDAO {
    @Insert
    abstract suspend fun insert(imageEntity: ImageEntity)

    @Insert
    abstract suspend fun insert(coordinateEntity: CoordinateEntity): Long

    @Transaction
    open suspend fun insert(image: Image) {
        val coordinateID = insert(coordinateEntity = image.coordinate.toCoordinateEntity())
        insert(imageEntity = image.toImageEntity(coordinateID = coordinateID))
    }

    @Transaction
    @RewriteQueriesToDropUnusedColumns
    @Query("SELECT * FROM Image WHERE Image.uri is :uri")
    abstract suspend fun selectImageBy(uri: String): ImageWithCoordinate?

    @Transaction
    @RewriteQueriesToDropUnusedColumns
    @Query(
        "SELECT * FROM Image JOIN Coordinate ON Image.coordinateID is Coordinate.id " +
                "WHERE Coordinate.latitude is :latitude AND Coordinate.longitude is :longitude"
    )
    abstract suspend fun selectImageBy(latitude: Double, longitude: Double): ImageWithCoordinate?

    @Transaction
    @RewriteQueriesToDropUnusedColumns
    @Query("SELECT * FROM Image")
    abstract fun selectImagesWithCoordinate(): Flow<List<ImageWithCoordinate>>

    @Query("DELETE FROM Image")
    abstract suspend fun deleteAll()
}