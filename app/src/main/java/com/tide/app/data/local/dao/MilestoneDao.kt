package com.tide.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.tide.app.data.local.entity.MilestoneEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MilestoneDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(milestones: List<MilestoneEntity>)

    @Update
    suspend fun update(milestone: MilestoneEntity)

    @Query("SELECT * FROM milestones ORDER BY id ASC")
    fun getAll(): Flow<List<MilestoneEntity>>

    @Query("SELECT * FROM milestones ORDER BY id ASC")
    suspend fun getAllSync(): List<MilestoneEntity>

    @Query("SELECT * FROM milestones WHERE addictionId = :addictionId OR addictionId IS NULL ORDER BY id ASC")
    fun getMilestonesForAddiction(addictionId: Long): Flow<List<MilestoneEntity>>

    @Query("SELECT * FROM milestones WHERE addictionId = :addictionId OR addictionId IS NULL ORDER BY id ASC")
    suspend fun getMilestonesForAddictionSync(addictionId: Long): List<MilestoneEntity>

    @Query("SELECT * FROM milestones WHERE type = :type LIMIT 1")
    suspend fun getByType(type: String): MilestoneEntity?

    @Query("SELECT * FROM milestones WHERE type = :type AND (addictionId = :addictionId OR (:addictionId IS NULL AND addictionId IS NULL)) LIMIT 1")
    suspend fun getByTypeAndAddiction(type: String, addictionId: Long?): MilestoneEntity?

    @Query("DELETE FROM milestones")
    suspend fun deleteAll()
}
