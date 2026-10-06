package com.tide.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.tide.app.data.local.entity.UserSettingsEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserSettingsDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(settings: UserSettingsEntity)

    @Query("SELECT * FROM user_settings WHERE id = 1 LIMIT 1")
    fun getSettings(): Flow<UserSettingsEntity?>

    @Query("SELECT * FROM user_settings WHERE id = 1 LIMIT 1")
    suspend fun getSettingsSync(): UserSettingsEntity?

    @Query("DELETE FROM user_settings")
    suspend fun deleteAll()
}
