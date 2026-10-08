package com.tide.app.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.tide.app.data.local.dao.AddictionDao
import com.tide.app.data.local.dao.ConsumptionEntryDao
import com.tide.app.data.local.dao.CravingEntryDao
import com.tide.app.data.local.dao.DailyCheckInDao
import com.tide.app.data.local.dao.GoalDao
import com.tide.app.data.local.dao.MilestoneDao
import com.tide.app.data.local.dao.NotificationPreferenceDao
import com.tide.app.data.local.dao.ProductDao
import com.tide.app.data.local.dao.RecoveryGoalDao
import com.tide.app.data.local.dao.SlipEntryDao
import com.tide.app.data.local.dao.TriggerDao
import com.tide.app.data.local.dao.UserSettingsDao
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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        AddictionEntity::class,
        ProductEntity::class,
        ConsumptionEntryEntity::class,
        CravingEntryEntity::class,
        SlipEntryEntity::class,
        DailyCheckInEntity::class,
        TriggerEntity::class,
        GoalEntity::class,
        RecoveryGoalEntity::class,
        MilestoneEntity::class,
        NotificationPreferenceEntity::class,
        UserSettingsEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class TideDatabase : RoomDatabase() {

    abstract fun addictionDao(): AddictionDao
    abstract fun productDao(): ProductDao
    abstract fun consumptionEntryDao(): ConsumptionEntryDao
    abstract fun cravingEntryDao(): CravingEntryDao
    abstract fun slipEntryDao(): SlipEntryDao
    abstract fun dailyCheckInDao(): DailyCheckInDao
    abstract fun triggerDao(): TriggerDao
    abstract fun goalDao(): GoalDao
    abstract fun recoveryGoalDao(): RecoveryGoalDao
    abstract fun milestoneDao(): MilestoneDao
    abstract fun notificationPreferenceDao(): NotificationPreferenceDao
    abstract fun userSettingsDao(): UserSettingsDao

    companion object {
        @Volatile
        private var INSTANCE: TideDatabase? = null

        val MIGRATION_1_2 = object : androidx.room.migration.Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE addictions ADD COLUMN hasFinancialImpact INTEGER NOT NULL DEFAULT 1")
                db.execSQL("ALTER TABLE addictions ADD COLUMN hasTimeImpact INTEGER NOT NULL DEFAULT 0")
                db.execSQL("ALTER TABLE addictions ADD COLUMN unitType TEXT NOT NULL DEFAULT 'COUNT'")
                db.execSQL("ALTER TABLE addictions ADD COLUMN isArchived INTEGER NOT NULL DEFAULT 0")
                db.execSQL("ALTER TABLE addictions ADD COLUMN archivedAt INTEGER DEFAULT NULL")
                db.execSQL("ALTER TABLE addictions ADD COLUMN displayOrder INTEGER NOT NULL DEFAULT 0")
                db.execSQL("ALTER TABLE addictions ADD COLUMN icon TEXT NOT NULL DEFAULT 'default'")
                db.execSQL("ALTER TABLE addictions ADD COLUMN color TEXT NOT NULL DEFAULT '#0A84FF'")

                db.execSQL("ALTER TABLE milestones ADD COLUMN addictionId INTEGER DEFAULT NULL")
                db.execSQL("ALTER TABLE notification_preferences ADD COLUMN addictionId INTEGER DEFAULT NULL")
            }
        }

        fun getInstance(context: Context): TideDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    TideDatabase::class.java,
                    "tide_database"
                )
                    .addMigrations(MIGRATION_1_2)
                    .addCallback(DatabaseCallback())
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private class DatabaseCallback : Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                CoroutineScope(Dispatchers.IO).launch {
                    INSTANCE?.let { database ->
                        populateInitialData(database)
                    }
                }
            }

            private suspend fun populateInitialData(db: TideDatabase) {
                // Prepopulate standard triggers
                val triggers = listOf(
                    TriggerEntity(name = "Stress", category = "Emotional"),
                    TriggerEntity(name = "Boredom", category = "Emotional"),
                    TriggerEntity(name = "Social situation", category = "Social"),
                    TriggerEntity(name = "After food", category = "Habit"),
                    TriggerEntity(name = "Alcohol", category = "Physical"),
                    TriggerEntity(name = "Work", category = "Situational"),
                    TriggerEntity(name = "Anxiety", category = "Emotional"),
                    TriggerEntity(name = "Habit/routine", category = "Habit"),
                    TriggerEntity(name = "Environment", category = "Situational"),
                    TriggerEntity(name = "Other", category = "General")
                )
                db.triggerDao().insertAll(triggers)

                // Prepopulate standard milestones
                val milestones = listOf(
                    MilestoneEntity(
                        type = "STREAK_1D",
                        title = "First Step",
                        description = "24 hours clean",
                        targetValue = 1.0
                    ),
                    MilestoneEntity(
                        type = "STREAK_7D",
                        title = "Finding Your Rhythm",
                        description = "7 days clean",
                        targetValue = 7.0
                    ),
                    MilestoneEntity(
                        type = "STREAK_14D",
                        title = "Two Weeks",
                        description = "14 days clean",
                        targetValue = 14.0
                    ),
                    MilestoneEntity(
                        type = "STREAK_30D",
                        title = "One Month",
                        description = "30 days clean",
                        targetValue = 30.0
                    ),
                    MilestoneEntity(
                        type = "CRAVINGS_10",
                        title = "Awareness",
                        description = "10 cravings logged or resisted",
                        targetValue = 10.0
                    ),
                    MilestoneEntity(
                        type = "CRAVINGS_50",
                        title = "Mastery",
                        description = "50 cravings logged or resisted",
                        targetValue = 50.0
                    ),
                    MilestoneEntity(
                        type = "SAVED_5000",
                        title = "Pocket Saver",
                        description = "₹5,000 saved from baseline",
                        targetValue = 5000.0
                    ),
                    MilestoneEntity(
                        type = "SAVED_10000",
                        title = "Financial Freedom",
                        description = "₹10,000 saved from baseline",
                        targetValue = 10000.0
                    ),
                    MilestoneEntity(
                        type = "REDUCTION_50",
                        title = "Major Shift",
                        description = "50% consumption reduction achieved",
                        targetValue = 50.0
                    )
                )
                db.milestoneDao().insertAll(milestones)

                // Prepopulate notification preferences
                val prefs = listOf(
                    NotificationPreferenceEntity(type = "daily_checkin", enabled = true, time = "20:00"),
                    NotificationPreferenceEntity(type = "goal_reminder", enabled = true, time = "09:00"),
                    NotificationPreferenceEntity(type = "milestone", enabled = true, time = "12:00"),
                    NotificationPreferenceEntity(type = "high_risk", enabled = false, time = "18:00")
                )
                db.notificationPreferenceDao().insertAll(prefs)

                // Initial user settings
                db.userSettingsDao().insertOrUpdate(UserSettingsEntity())
            }
        }
    }
}
