package com.tide.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.tide.app.data.local.entity.ProductEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProduct(product: ProductEntity): Long

    @Update
    suspend fun updateProduct(product: ProductEntity)

    @Query("SELECT * FROM products WHERE addictionId = :addictionId ORDER BY id DESC LIMIT 1")
    fun getProductForAddiction(addictionId: Long): Flow<ProductEntity?>

    @Query("SELECT * FROM products WHERE addictionId = :addictionId ORDER BY id DESC LIMIT 1")
    suspend fun getProductForAddictionSync(addictionId: Long): ProductEntity?

    @Query("DELETE FROM products WHERE addictionId = :addictionId")
    suspend fun deleteForAddiction(addictionId: Long)

    @Query("DELETE FROM products")
    suspend fun deleteAll()
}
