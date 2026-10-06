package com.tide.app.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.tide.app.data.local.entity.RecoveryGoalEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RecoveryGoalDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(recoveryGoal: RecoveryGoalEntity): Long

    @Update
    suspend fun update(recoveryGoal: RecoveryGoalEntity)

    @Delete
    suspend fun delete(recoveryGoal: RecoveryGoalEntity)

    @Query("SELECT * FROM recovery_goals ORDER BY createdAt DESC")
    fun getAll(): Flow<List<RecoveryGoalEntity>>

    @Query("SELECT * FROM recovery_goals ORDER BY createdAt DESC")
    suspend fun getAllSync(): List<RecoveryGoalEntity>

    @Query("DELETE FROM recovery_goals")
    suspend fun deleteAll()
}
