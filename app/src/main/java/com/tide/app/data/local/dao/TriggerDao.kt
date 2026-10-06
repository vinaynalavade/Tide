package com.tide.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.tide.app.data.local.entity.TriggerEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TriggerDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(triggers: List<TriggerEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(trigger: TriggerEntity): Long

    @Query("SELECT * FROM triggers ORDER BY name ASC")
    fun getAllTriggers(): Flow<List<TriggerEntity>>

    @Query("SELECT * FROM triggers ORDER BY name ASC")
    suspend fun getAllTriggersSync(): List<TriggerEntity>

    @Query("SELECT * FROM triggers WHERE id = :id LIMIT 1")
    suspend fun getTriggerById(id: Long): TriggerEntity?

    @Query("DELETE FROM triggers")
    suspend fun deleteAll()
}
