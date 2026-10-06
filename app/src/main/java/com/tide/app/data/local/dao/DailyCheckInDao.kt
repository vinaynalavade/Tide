package com.tide.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.tide.app.data.local.entity.DailyCheckInEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DailyCheckInDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(checkIn: DailyCheckInEntity): Long

    @Query("SELECT * FROM daily_check_ins WHERE addictionId = :addictionId AND date = :date LIMIT 1")
    fun getCheckInForDate(addictionId: Long, date: String): Flow<DailyCheckInEntity?>

    @Query("SELECT * FROM daily_check_ins WHERE addictionId = :addictionId AND date = :date LIMIT 1")
    suspend fun getCheckInForDateSync(addictionId: Long, date: String): DailyCheckInEntity?

    @Query("SELECT * FROM daily_check_ins WHERE addictionId = :addictionId ORDER BY date DESC")
    fun getAllCheckIns(addictionId: Long): Flow<List<DailyCheckInEntity>>

    @Query("SELECT * FROM daily_check_ins WHERE addictionId = :addictionId ORDER BY date DESC")
    suspend fun getAllCheckInsSync(addictionId: Long): List<DailyCheckInEntity>

    @Query("DELETE FROM daily_check_ins")
    suspend fun deleteAll()
}
