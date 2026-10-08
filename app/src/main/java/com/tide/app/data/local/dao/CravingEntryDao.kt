package com.tide.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.tide.app.data.local.entity.CravingEntryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CravingEntryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entry: CravingEntryEntity): Long

    @Query("SELECT * FROM craving_entries WHERE addictionId = :addictionId ORDER BY timestamp DESC")
    fun getEntriesForAddiction(addictionId: Long): Flow<List<CravingEntryEntity>>

    @Query("SELECT * FROM craving_entries WHERE addictionId = :addictionId ORDER BY timestamp DESC")
    suspend fun getEntriesForAddictionSync(addictionId: Long): List<CravingEntryEntity>

    @Query("SELECT * FROM craving_entries WHERE addictionId = :addictionId AND timestamp >= :startTime AND timestamp <= :endTime ORDER BY timestamp ASC")
    fun getEntriesForAddictionBetween(addictionId: Long, startTime: Long, endTime: Long): Flow<List<CravingEntryEntity>>

    @Query("SELECT * FROM craving_entries WHERE addictionId = :addictionId ORDER BY timestamp DESC, id DESC LIMIT 1")
    suspend fun getLatestEntry(addictionId: Long): CravingEntryEntity?

    @Query("DELETE FROM craving_entries WHERE id = :id")
    suspend fun deleteById(id: Long): Int

    @Query("DELETE FROM craving_entries")
    suspend fun deleteAll()
}
