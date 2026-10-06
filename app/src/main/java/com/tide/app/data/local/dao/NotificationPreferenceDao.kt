package com.tide.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.tide.app.data.local.entity.NotificationPreferenceEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface NotificationPreferenceDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(preferences: List<NotificationPreferenceEntity>)

    @Update
    suspend fun update(preference: NotificationPreferenceEntity)

    @Query("SELECT * FROM notification_preferences ORDER BY id ASC")
    fun getAll(): Flow<List<NotificationPreferenceEntity>>

    @Query("SELECT * FROM notification_preferences ORDER BY id ASC")
    suspend fun getAllSync(): List<NotificationPreferenceEntity>

    @Query("SELECT * FROM notification_preferences WHERE type = :type LIMIT 1")
    suspend fun getByType(type: String): NotificationPreferenceEntity?

    @Query("DELETE FROM notification_preferences")
    suspend fun deleteAll()
}
