package com.tide.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.tide.app.data.local.entity.AddictionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AddictionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAddiction(addiction: AddictionEntity): Long

    @Update
    suspend fun updateAddiction(addiction: AddictionEntity)

    @Query("SELECT * FROM addictions WHERE active = 1 ORDER BY id DESC LIMIT 1")
    fun getActiveAddiction(): Flow<AddictionEntity?>

    @Query("SELECT * FROM addictions WHERE active = 1 ORDER BY id DESC LIMIT 1")
    suspend fun getActiveAddictionSync(): AddictionEntity?

    @Query("SELECT * FROM addictions ORDER BY id DESC")
    fun getAllAddictions(): Flow<List<AddictionEntity>>

    @Query("SELECT * FROM addictions WHERE id = :id")
    suspend fun getAddictionById(id: Long): AddictionEntity?

    @Query("DELETE FROM addictions")
    suspend fun deleteAll()
}
