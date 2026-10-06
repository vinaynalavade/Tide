package com.tide.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.tide.app.data.local.entity.GoalEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface GoalDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(goal: GoalEntity): Long

    @Update
    suspend fun update(goal: GoalEntity)

    @Query("SELECT * FROM goals WHERE addictionId = :addictionId AND active = 1 ORDER BY id DESC LIMIT 1")
    fun getActiveGoal(addictionId: Long): Flow<GoalEntity?>

    @Query("SELECT * FROM goals WHERE addictionId = :addictionId AND active = 1 ORDER BY id DESC LIMIT 1")
    suspend fun getActiveGoalSync(addictionId: Long): GoalEntity?

    @Query("DELETE FROM goals")
    suspend fun deleteAll()
}
