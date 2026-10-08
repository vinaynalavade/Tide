package com.tide.app.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.tide.app.data.local.entity.ConsumptionEntryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ConsumptionEntryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entry: ConsumptionEntryEntity): Long

    @Delete
    suspend fun delete(entry: ConsumptionEntryEntity)

    @Query("SELECT * FROM consumption_entries WHERE addictionId = :addictionId ORDER BY timestamp DESC")
    fun getEntriesForAddiction(addictionId: Long): Flow<List<ConsumptionEntryEntity>>

    @Query("SELECT * FROM consumption_entries WHERE addictionId = :addictionId ORDER BY timestamp DESC")
    suspend fun getEntriesForAddictionSync(addictionId: Long): List<ConsumptionEntryEntity>

    @Query("SELECT * FROM consumption_entries WHERE addictionId = :addictionId AND timestamp >= :startTime AND timestamp <= :endTime ORDER BY timestamp ASC")
    fun getEntriesForAddictionBetween(addictionId: Long, startTime: Long, endTime: Long): Flow<List<ConsumptionEntryEntity>>

    @Query("SELECT * FROM consumption_entries WHERE addictionId = :addictionId AND timestamp >= :startTime AND timestamp <= :endTime ORDER BY timestamp ASC")
    suspend fun getEntriesForAddictionBetweenSync(addictionId: Long, startTime: Long, endTime: Long): List<ConsumptionEntryEntity>

    @Query("SELECT * FROM consumption_entries WHERE addictionId = :addictionId ORDER BY timestamp DESC, id DESC LIMIT 1")
    suspend fun getLatestEntry(addictionId: Long): ConsumptionEntryEntity?

    @Query("DELETE FROM consumption_entries WHERE id = :id")
    suspend fun deleteById(id: Long): Int

    @Query("DELETE FROM consumption_entries")
    suspend fun deleteAll()
}
