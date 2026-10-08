package com.tide.app.data.repository

import androidx.room.withTransaction
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

    override fun getActiveHabits(): Flow<List<AddictionEntity>> {
        return database.addictionDao().getActiveAddictions()
    }

    override suspend fun getActiveHabitsSync(): List<AddictionEntity> = withContext(Dispatchers.IO) {
        database.addictionDao().getActiveAddictionsSync()
    }

    override fun getArchivedHabits(): Flow<List<AddictionEntity>> {
        return database.addictionDao().getArchivedAddictions()
    }

    override suspend fun getArchivedHabitsSync(): List<AddictionEntity> = withContext(Dispatchers.IO) {
        database.addictionDao().getArchivedAddictionsSync()
    }

    override fun getAllHabits(): Flow<List<AddictionEntity>> {
        return database.addictionDao().getAllAddictions()
    }

    override suspend fun getAllHabitsSync(): List<AddictionEntity> = withContext(Dispatchers.IO) {
        database.addictionDao().getAllAddictionsSync()
    }

    override fun getHabitById(id: Long): Flow<AddictionEntity?> {
        return database.addictionDao().getAddictionByIdFlow(id)
    }

    override suspend fun getHabitByIdSync(id: Long): AddictionEntity? = withContext(Dispatchers.IO) {
        database.addictionDao().getAddictionById(id)
    }

    override suspend fun createHabit(
        habit: AddictionEntity,
        product: ProductEntity?,
        goal: GoalEntity,
        defaultTriggers: List<String>
    ): Long = withContext(Dispatchers.IO) {
        database.withTransaction {
            val habitId = database.addictionDao().insertAddiction(habit)
            if (product != null) {
                database.productDao().insertProduct(product.copy(addictionId = habitId))
            }
            database.goalDao().insert(goal.copy(addictionId = habitId))

            for (triggerName in defaultTriggers) {
                database.triggerDao().insert(TriggerEntity(name = triggerName, category = habit.category))
            }
            habitId
        }
    }

    override suspend fun updateHabitConfig(
        habit: AddictionEntity,
        product: ProductEntity?,
        goal: GoalEntity?
    ) = withContext(Dispatchers.IO) {
        database.withTransaction {
            database.addictionDao().updateAddiction(habit)

            if (product != null) {
                val existingProduct = database.productDao().getProductForAddictionSync(habit.id)
                if (existingProduct != null) {
                    database.productDao().updateProduct(product.copy(id = existingProduct.id, addictionId = habit.id))
                } else {
                    database.productDao().insertProduct(product.copy(addictionId = habit.id))
                }
            }

            if (goal != null) {
                val existingGoal = database.goalDao().getActiveGoalSync(habit.id)
                if (existingGoal != null) {
                    database.goalDao().update(goal.copy(id = existingGoal.id, addictionId = habit.id))
                } else {
                    database.goalDao().insert(goal.copy(addictionId = habit.id))
                }
            }
        }
    }

    override suspend fun archiveHabit(id: Long) = withContext(Dispatchers.IO) {
        database.addictionDao().archiveAddiction(id, System.currentTimeMillis())
    }

    override suspend fun restoreHabit(id: Long) = withContext(Dispatchers.IO) {
        database.addictionDao().restoreAddiction(id)
    }

    override suspend fun deleteHabit(id: Long): Boolean = withContext(Dispatchers.IO) {
        database.withTransaction {
            database.productDao().deleteForAddiction(id)
            database.addictionDao().deleteById(id) > 0
        }
    }

    override suspend fun undoLastConsumption(addictionId: Long): Boolean = withContext(Dispatchers.IO) {
        val last = database.consumptionEntryDao().getLatestEntry(addictionId)
        if (last != null) {
            database.consumptionEntryDao().deleteById(last.id) > 0
        } else false
    }

    override suspend fun undoLastCraving(addictionId: Long): Boolean = withContext(Dispatchers.IO) {
        val last = database.cravingEntryDao().getLatestEntry(addictionId)
        if (last != null) {
            database.cravingEntryDao().deleteById(last.id) > 0
        } else false
    }

    override suspend fun undoLastSlip(addictionId: Long): Boolean = withContext(Dispatchers.IO) {
        val last = database.slipEntryDao().getLatestEntry(addictionId)
        if (last != null) {
            database.slipEntryDao().deleteById(last.id) > 0
        } else false
    }

    override suspend fun undoEntry(type: String, id: Long): Boolean = withContext(Dispatchers.IO) {
        when (type.uppercase()) {
            "CONSUMPTION" -> database.consumptionEntryDao().deleteById(id) > 0
            "CRAVING" -> database.cravingEntryDao().deleteById(id) > 0
            "SLIP" -> database.slipEntryDao().deleteById(id) > 0
            else -> false
        }
    }

    override suspend fun saveAddiction(addiction: AddictionEntity): Long = withContext(Dispatchers.IO) {
        database.addictionDao().insertAddiction(addiction)
    }

    override suspend fun updateAddiction(addiction: AddictionEntity) = withContext(Dispatchers.IO) {
        database.addictionDao().updateAddiction(addiction)
    }

    override suspend fun completeOnboardingTransaction(
        addiction: AddictionEntity,
        product: ProductEntity,
        goal: GoalEntity,
        defaultTriggers: List<String>
    ): Long = withContext(Dispatchers.IO) {
        database.withTransaction {
            val addictionId = database.addictionDao().insertAddiction(addiction)
            database.productDao().insertProduct(product.copy(addictionId = addictionId))
            database.goalDao().insert(goal.copy(addictionId = addictionId))

            for (triggerName in defaultTriggers) {
                database.triggerDao().insert(TriggerEntity(name = triggerName, category = "Default"))
            }

            database.notificationPreferenceDao().insertAll(
                listOf(
                    NotificationPreferenceEntity(
                        type = "DAILY_CHECKIN",
                        time = "20:00",
                        enabled = true
                    ),
                    NotificationPreferenceEntity(
                        type = "MILESTONES",
                        time = "09:00",
                        enabled = true
                    )
                )
            )

            database.userSettingsDao().insertOrUpdate(
                UserSettingsEntity(
                    id = 1,
                    onboardingCompleted = true
                )
            )

            addictionId
        }
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

    override fun getMilestonesForHabit(addictionId: Long): Flow<List<MilestoneEntity>> {
        return database.milestoneDao().getMilestonesForAddiction(addictionId)
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

    override suspend fun checkAndUpdateMilestonesForHabit(
        addictionId: Long,
        streakDays: Int,
        totalCravings: Int,
        totalSaved: Double,
        reductionPct: Double
    ) = withContext(Dispatchers.IO) {
        val habitMilestones = database.milestoneDao().getMilestonesForAddictionSync(addictionId)
        val now = System.currentTimeMillis()

        for (m in habitMilestones) {
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
                database.milestoneDao().update(m.copy(achievedAt = now, addictionId = addictionId))
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
        root.put("version", "1.1.0")
        root.put("build", 2)
        root.put("exportedAt", System.currentTimeMillis())

        val allHabits = database.addictionDao().getAllAddictionsSync()
        val habitsArray = JSONArray()

        for (habit in allHabits) {
            val habitObj = JSONObject().apply {
                put("id", habit.id)
                put("name", habit.name)
                put("category", habit.category)
                put("unit", habit.unit)
                put("customUnit", habit.customUnit)
                put("baselineAmount", habit.baselineAmount)
                put("baselineFrequency", habit.baselineFrequency)
                put("baselineCost", habit.baselineCost)
                put("currency", habit.currency)
                put("goalType", habit.goalType)
                put("startDate", habit.startDate)
                put("active", habit.active)
                put("hasFinancialImpact", habit.hasFinancialImpact)
                put("hasTimeImpact", habit.hasTimeImpact)
                put("unitType", habit.unitType)
                put("isArchived", habit.isArchived)
                put("archivedAt", habit.archivedAt)
                put("icon", habit.icon)
            }

            // Product
            val product = database.productDao().getProductForAddictionSync(habit.id)
            if (product != null) {
                habitObj.put("product", JSONObject().apply {
                    put("brand", product.brand)
                    put("productName", product.productName)
                    put("price", product.price)
                    put("pricingBasis", product.pricingBasis)
                    put("unitsPerBasis", product.unitsPerBasis)
                    put("unitCost", product.unitCost)
                })
            }

            // Goal
            val goal = database.goalDao().getActiveGoalSync(habit.id)
            if (goal != null) {
                habitObj.put("goal", JSONObject().apply {
                    put("type", goal.type)
                    put("targetAmount", goal.targetAmount)
                    put("targetFrequency", goal.targetFrequency)
                })
            }

            // Consumptions
            val consumptions = database.consumptionEntryDao().getEntriesForAddictionSync(habit.id)
            val cArray = JSONArray()
            consumptions.forEach {
                cArray.put(JSONObject().apply {
                    put("timestamp", it.timestamp)
                    put("amount", it.amount)
                    put("cost", it.calculatedCost)
                    put("note", it.note)
                })
            }
            habitObj.put("consumptionEntries", cArray)

            // Cravings
            val cravings = database.cravingEntryDao().getEntriesForAddictionSync(habit.id)
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
            habitObj.put("cravingEntries", crArray)

            // Slips
            val slips = database.slipEntryDao().getEntriesForAddictionSync(habit.id)
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
            habitObj.put("slipEntries", sArray)

            // Check-ins
            val checkIns = database.dailyCheckInDao().getAllCheckInsSync(habit.id)
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
            habitObj.put("dailyCheckIns", ciArray)

            habitsArray.put(habitObj)
        }
        root.put("habits", habitsArray)

        // Single addiction backward-compatibility node
        val activeAddiction = database.addictionDao().getActiveAddictionSync()
        if (activeAddiction != null) {
            val addObj = JSONObject().apply {
                put("name", activeAddiction.name)
                put("category", activeAddiction.category)
                put("unit", activeAddiction.unit)
                put("baselineAmount", activeAddiction.baselineAmount)
                put("baselineCost", activeAddiction.baselineCost)
                put("goalType", activeAddiction.goalType)
                put("startDate", activeAddiction.startDate)
            }
            root.put("addiction", addObj)
        }

        root.toString(2)
    }
}
