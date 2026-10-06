package com.tide.app.data.repository

import com.tide.app.data.local.entity.AddictionEntity
import com.tide.app.data.local.entity.ConsumptionEntryEntity
import com.tide.app.data.local.entity.CravingEntryEntity
import com.tide.app.data.local.entity.DailyCheckInEntity
import com.tide.app.data.local.entity.GoalEntity
import com.tide.app.data.local.entity.MilestoneEntity
import com.tide.app.data.local.entity.NotificationPreferenceEntity
import com.tide.app.data.local.entity.ProductEntity
import com.tide.app.data.local.entity.RecoveryGoalEntity
import com.tide.app.data.local.entity.SlipEntryEntity
import com.tide.app.data.local.entity.TriggerEntity
import com.tide.app.data.local.entity.UserSettingsEntity
import kotlinx.coroutines.flow.Flow

interface TideRepository {
    fun getActiveAddiction(): Flow<AddictionEntity?>
    suspend fun getActiveAddictionSync(): AddictionEntity?
    suspend fun saveAddiction(addiction: AddictionEntity): Long
    suspend fun updateAddiction(addiction: AddictionEntity)

    fun getProductForAddiction(addictionId: Long): Flow<ProductEntity?>
    suspend fun getProductForAddictionSync(addictionId: Long): ProductEntity?
    suspend fun saveProduct(product: ProductEntity): Long

    suspend fun logConsumption(entry: ConsumptionEntryEntity): Long
    suspend fun deleteConsumption(entry: ConsumptionEntryEntity)
    fun getConsumptionEntries(addictionId: Long): Flow<List<ConsumptionEntryEntity>>

    suspend fun logCraving(entry: CravingEntryEntity): Long
    fun getCravingEntries(addictionId: Long): Flow<List<CravingEntryEntity>>

    suspend fun logSlip(entry: SlipEntryEntity): Long
    fun getSlipEntries(addictionId: Long): Flow<List<SlipEntryEntity>>

    suspend fun saveDailyCheckIn(checkIn: DailyCheckInEntity): Long
    fun getDailyCheckIn(addictionId: Long, date: String): Flow<DailyCheckInEntity?>
    suspend fun getDailyCheckInSync(addictionId: Long, date: String): DailyCheckInEntity?
    fun getAllDailyCheckIns(addictionId: Long): Flow<List<DailyCheckInEntity>>

    fun getAllTriggers(): Flow<List<TriggerEntity>>
    suspend fun addTrigger(name: String, category: String = "General"): Long

    fun getActiveGoal(addictionId: Long): Flow<GoalEntity?>
    suspend fun saveGoal(goal: GoalEntity): Long

    fun getRecoveryGoals(): Flow<List<RecoveryGoalEntity>>
    suspend fun addRecoveryGoal(name: String, targetAmount: Double): Long
    suspend fun updateRecoveryGoal(goal: RecoveryGoalEntity)
    suspend fun deleteRecoveryGoal(goal: RecoveryGoalEntity)

    fun getMilestones(): Flow<List<MilestoneEntity>>
    suspend fun checkAndUpdateMilestones(
        streakDays: Int,
        totalCravings: Int,
        totalSaved: Double,
        reductionPct: Double
    )

    fun getNotificationPreferences(): Flow<List<NotificationPreferenceEntity>>
    suspend fun updateNotificationPreference(preference: NotificationPreferenceEntity)

    fun getUserSettings(): Flow<UserSettingsEntity?>
    suspend fun updateUserSettings(settings: UserSettingsEntity)

    suspend fun deleteAllData()
    suspend fun exportAllDataJson(): String
}
