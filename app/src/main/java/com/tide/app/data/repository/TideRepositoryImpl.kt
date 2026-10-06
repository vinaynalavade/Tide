package com.tide.app.data.repository

import com.tide.app.data.local.TideDatabase
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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject

class TideRepositoryImpl(
    private val database: TideDatabase
) : TideRepository {

    override fun getActiveAddiction(): Flow<AddictionEntity?> {
        return database.addictionDao().getActiveAddiction()
    }

    override suspend fun getActiveAddictionSync(): AddictionEntity? = withContext(Dispatchers.IO) {
        database.addictionDao().getActiveAddictionSync()
    }

    override suspend fun saveAddiction(addiction: AddictionEntity): Long = withContext(Dispatchers.IO) {
        database.addictionDao().insertAddiction(addiction)
    }

    override suspend fun updateAddiction(addiction: AddictionEntity) = withContext(Dispatchers.IO) {
        database.addictionDao().updateAddiction(addiction)
    }

    override fun getProductForAddiction(addictionId: Long): Flow<ProductEntity?> {
        return database.productDao().getProductForAddiction(addictionId)
    }

    override suspend fun getProductForAddictionSync(addictionId: Long): ProductEntity? = withContext(Dispatchers.IO) {
        database.productDao().getProductForAddictionSync(addictionId)
    }

    override suspend fun saveProduct(product: ProductEntity): Long = withContext(Dispatchers.IO) {
        database.productDao().insertProduct(product)
    }

    override suspend fun logConsumption(entry: ConsumptionEntryEntity): Long = withContext(Dispatchers.IO) {
        database.consumptionEntryDao().insert(entry)
    }

    override suspend fun deleteConsumption(entry: ConsumptionEntryEntity) = withContext(Dispatchers.IO) {
        database.consumptionEntryDao().delete(entry)
    }

    override fun getConsumptionEntries(addictionId: Long): Flow<List<ConsumptionEntryEntity>> {
        return database.consumptionEntryDao().getEntriesForAddiction(addictionId)
    }

    override suspend fun logCraving(entry: CravingEntryEntity): Long = withContext(Dispatchers.IO) {
        database.cravingEntryDao().insert(entry)
    }

    override fun getCravingEntries(addictionId: Long): Flow<List<CravingEntryEntity>> {
        return database.cravingEntryDao().getEntriesForAddiction(addictionId)
    }

    override suspend fun logSlip(entry: SlipEntryEntity): Long = withContext(Dispatchers.IO) {
        database.slipEntryDao().insert(entry)
    }

    override fun getSlipEntries(addictionId: Long): Flow<List<SlipEntryEntity>> {
        return database.slipEntryDao().getEntriesForAddiction(addictionId)
    }

    override suspend fun saveDailyCheckIn(checkIn: DailyCheckInEntity): Long = withContext(Dispatchers.IO) {
        database.dailyCheckInDao().insertOrUpdate(checkIn)
    }

    override fun getDailyCheckIn(addictionId: Long, date: String): Flow<DailyCheckInEntity?> {
        return database.dailyCheckInDao().getCheckInForDate(addictionId, date)
    }

    override suspend fun getDailyCheckInSync(addictionId: Long, date: String): DailyCheckInEntity? = withContext(Dispatchers.IO) {
        database.dailyCheckInDao().getCheckInForDateSync(addictionId, date)
    }

    override fun getAllDailyCheckIns(addictionId: Long): Flow<List<DailyCheckInEntity>> {
        return database.dailyCheckInDao().getAllCheckIns(addictionId)
    }

    override fun getAllTriggers(): Flow<List<TriggerEntity>> {
        return database.triggerDao().getAllTriggers()
    }

    override suspend fun addTrigger(name: String, category: String): Long = withContext(Dispatchers.IO) {
        database.triggerDao().insert(TriggerEntity(name = name.trim(), category = category))
    }

    override fun getActiveGoal(addictionId: Long): Flow<GoalEntity?> {
        return database.goalDao().getActiveGoal(addictionId)
    }

    override suspend fun saveGoal(goal: GoalEntity): Long = withContext(Dispatchers.IO) {
        database.goalDao().insert(goal)
    }

    override fun getRecoveryGoals(): Flow<List<RecoveryGoalEntity>> {
        return database.recoveryGoalDao().getAll()
    }

    override suspend fun addRecoveryGoal(name: String, targetAmount: Double): Long = withContext(Dispatchers.IO) {
        database.recoveryGoalDao().insert(
            RecoveryGoalEntity(name = name.trim(), targetAmount = targetAmount)
        )
    }

    override suspend fun updateRecoveryGoal(goal: RecoveryGoalEntity) = withContext(Dispatchers.IO) {
        database.recoveryGoalDao().update(goal)
    }

    override suspend fun deleteRecoveryGoal(goal: RecoveryGoalEntity) = withContext(Dispatchers.IO) {
        database.recoveryGoalDao().delete(goal)
    }

    override fun getMilestones(): Flow<List<MilestoneEntity>> {
        return database.milestoneDao().getAll()
    }

    override suspend fun checkAndUpdateMilestones(
        streakDays: Int,
        totalCravings: Int,
        totalSaved: Double,
        reductionPct: Double
    ) = withContext(Dispatchers.IO) {
        val all = database.milestoneDao().getAllSync()
        val now = System.currentTimeMillis()

        for (m in all) {
            if (m.achievedAt != null) continue // Already unlocked

            val isAchieved = when (m.type) {
                "STREAK_1D" -> streakDays >= 1
                "STREAK_7D" -> streakDays >= 7
                "STREAK_14D" -> streakDays >= 14
                "STREAK_30D" -> streakDays >= 30
                "CRAVINGS_10" -> totalCravings >= 10
                "CRAVINGS_50" -> totalCravings >= 50
                "SAVED_5000" -> totalSaved >= 5000.0
                "SAVED_10000" -> totalSaved >= 10000.0
                "REDUCTION_50" -> reductionPct >= 50.0
                else -> false
            }

            if (isAchieved) {
                database.milestoneDao().update(m.copy(achievedAt = now))
            }
        }
    }

    override fun getNotificationPreferences(): Flow<List<NotificationPreferenceEntity>> {
        return database.notificationPreferenceDao().getAll()
    }

    override suspend fun updateNotificationPreference(preference: NotificationPreferenceEntity) = withContext(Dispatchers.IO) {
        database.notificationPreferenceDao().update(preference)
    }

    override fun getUserSettings(): Flow<UserSettingsEntity?> {
        return database.userSettingsDao().getSettings()
    }

    override suspend fun updateUserSettings(settings: UserSettingsEntity) = withContext(Dispatchers.IO) {
        database.userSettingsDao().insertOrUpdate(settings)
    }

    override suspend fun deleteAllData() = withContext(Dispatchers.IO) {
        database.consumptionEntryDao().deleteAll()
        database.cravingEntryDao().deleteAll()
        database.slipEntryDao().deleteAll()
        database.dailyCheckInDao().deleteAll()
        database.recoveryGoalDao().deleteAll()
        database.goalDao().deleteAll()
        database.productDao().deleteAll()
        database.addictionDao().deleteAll()
        database.userSettingsDao().insertOrUpdate(UserSettingsEntity(id = 1, onboardingCompleted = false))
    }

    override suspend fun exportAllDataJson(): String = withContext(Dispatchers.IO) {
        val root = JSONObject()
        root.put("app", "Tide")
        root.put("version", "1.0.0")
        root.put("build", 1)
        root.put("exportedAt", System.currentTimeMillis())

        val addiction = database.addictionDao().getActiveAddictionSync()
        if (addiction != null) {
            val addObj = JSONObject().apply {
                put("name", addiction.name)
                put("category", addiction.category)
                put("unit", addiction.unit)
                put("baselineAmount", addiction.baselineAmount)
                put("baselineCost", addiction.baselineCost)
                put("goalType", addiction.goalType)
                put("startDate", addiction.startDate)
            }
            root.put("addiction", addObj)

            val consumptions = database.consumptionEntryDao().getEntriesForAddictionSync(addiction.id)
            val cArray = JSONArray()
            consumptions.forEach {
                cArray.put(JSONObject().apply {
                    put("timestamp", it.timestamp)
                    put("amount", it.amount)
                    put("cost", it.calculatedCost)
                    put("note", it.note)
                })
            }
            root.put("consumptionEntries", cArray)

            val cravings = database.cravingEntryDao().getEntriesForAddictionSync(addiction.id)
            val crArray = JSONArray()
            cravings.forEach {
                crArray.put(JSONObject().apply {
                    put("timestamp", it.timestamp)
                    put("intensity", it.intensity)
                    put("resisted", it.resisted)
                    put("mood", it.mood)
                    put("note", it.note)
                })
            }
            root.put("cravingEntries", crArray)

            val slips = database.slipEntryDao().getEntriesForAddictionSync(addiction.id)
            val sArray = JSONArray()
            slips.forEach {
                sArray.put(JSONObject().apply {
                    put("timestamp", it.timestamp)
                    put("amount", it.amount)
                    put("cost", it.cost)
                    put("mood", it.mood)
                    put("note", it.note)
                })
            }
            root.put("slipEntries", sArray)

            val checkIns = database.dailyCheckInDao().getAllCheckInsSync(addiction.id)
            val ciArray = JSONArray()
            checkIns.forEach {
                ciArray.put(JSONObject().apply {
                    put("date", it.date)
                    put("mood", it.mood)
                    put("cravingOccurred", it.cravingOccurred)
                    put("cravingIntensity", it.cravingIntensity)
                    put("note", it.note)
                })
            }
            root.put("dailyCheckIns", ciArray)
        }

        root.toString(2)
    }
}
