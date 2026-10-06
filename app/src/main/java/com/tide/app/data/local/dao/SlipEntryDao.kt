package com.tide.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.tide.app.data.local.entity.SlipEntryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SlipEntryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entry: SlipEntryEntity): Long

    @Query("SELECT * FROM slip_entries WHERE addictionId = :addictionId ORDER BY timestamp DESC")
    fun getEntriesForAddiction(addictionId: Long): Flow<List<SlipEntryEntity>>

    @Query("SELECT * FROM slip_entries WHERE addictionId = :addictionId ORDER BY timestamp DESC")
    suspend fun getEntriesForAddictionSync(addictionId: Long): List<SlipEntryEntity>

    @Query("SELECT * FROM slip_entries WHERE addictionId = :addictionId AND timestamp >= :startTime AND timestamp <= :endTime ORDER BY timestamp ASC")
    fun getEntriesForAddictionBetween(addictionId: Long, startTime: Long, endTime: Long): Flow<List<SlipEntryEntity>>

    @Query("SELECT * FROM slip_entries WHERE addictionId = :addictionId AND timestamp >= :startTime AND timestamp <= :endTime ORDER BY timestamp ASC")
    suspend fun getEntriesForAddictionBetweenSync(addictionId: Long, startTime: Long, endTime: Long): List<SlipEntryEntity>

    @Query("DELETE FROM slip_entries")
    suspend fun deleteAll()
}
