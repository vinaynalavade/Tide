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

    @Query("SELECT * FROM addictions WHERE active = 1 AND isArchived = 0 ORDER BY displayOrder ASC, id ASC LIMIT 1")
    fun getActiveAddiction(): Flow<AddictionEntity?>

    @Query("SELECT * FROM addictions WHERE active = 1 AND isArchived = 0 ORDER BY displayOrder ASC, id ASC LIMIT 1")
    suspend fun getActiveAddictionSync(): AddictionEntity?

    @Query("SELECT * FROM addictions WHERE active = 1 AND isArchived = 0 ORDER BY displayOrder ASC, id ASC")
    fun getActiveAddictions(): Flow<List<AddictionEntity>>

    @Query("SELECT * FROM addictions WHERE active = 1 AND isArchived = 0 ORDER BY displayOrder ASC, id ASC")
    suspend fun getActiveAddictionsSync(): List<AddictionEntity>

    @Query("SELECT * FROM addictions WHERE isArchived = 1 ORDER BY archivedAt DESC, id DESC")
    fun getArchivedAddictions(): Flow<List<AddictionEntity>>

    @Query("SELECT * FROM addictions WHERE isArchived = 1 ORDER BY archivedAt DESC, id DESC")
    suspend fun getArchivedAddictionsSync(): List<AddictionEntity>

    @Query("SELECT * FROM addictions ORDER BY displayOrder ASC, id ASC")
    fun getAllAddictions(): Flow<List<AddictionEntity>>

    @Query("SELECT * FROM addictions ORDER BY displayOrder ASC, id ASC")
    suspend fun getAllAddictionsSync(): List<AddictionEntity>

    @Query("SELECT * FROM addictions WHERE id = :id")
    suspend fun getAddictionById(id: Long): AddictionEntity?

    @Query("SELECT * FROM addictions WHERE id = :id")
    fun getAddictionByIdFlow(id: Long): Flow<AddictionEntity?>

    @Query("UPDATE addictions SET isArchived = 1, archivedAt = :timestamp WHERE id = :id")
    suspend fun archiveAddiction(id: Long, timestamp: Long)

    @Query("UPDATE addictions SET isArchived = 0, archivedAt = NULL WHERE id = :id")
    suspend fun restoreAddiction(id: Long)

    @Query("DELETE FROM addictions WHERE id = :id")
    suspend fun deleteById(id: Long): Int

    @Query("DELETE FROM addictions")
    suspend fun deleteAll()
}
