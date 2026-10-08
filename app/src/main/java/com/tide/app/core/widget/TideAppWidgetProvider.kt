package com.tide.app.core.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.view.View
import android.widget.RemoteViews
import com.tide.app.MainActivity
import com.tide.app.R
import com.tide.app.core.calculation.TideCalculationEngine
import com.tide.app.data.local.TideDatabase
import com.tide.app.data.local.entity.ConsumptionEntryEntity
import com.tide.app.data.local.entity.CravingEntryEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDate
import kotlin.math.max

class TideAppWidgetProvider : AppWidgetProvider() {

    companion object {
        const val ACTION_LOG_UNIT = "com.tide.app.widget.ACTION_LOG_UNIT"
        const val ACTION_LOG_CRAVING = "com.tide.app.widget.ACTION_LOG_CRAVING"
        const val ACTION_UNDO = "com.tide.app.widget.ACTION_UNDO"
        const val ACTION_NEXT_HABIT = "com.tide.app.widget.ACTION_NEXT_HABIT"

        const val EXTRA_HABIT_ID = "extra_habit_id"
        const val EXTRA_APPWIDGET_ID = "extra_appwidget_id"

        private const val PREFS_NAME = "tide_widget_prefs"
        private const val KEY_LAST_TYPE = "last_action_type"
        private const val KEY_LAST_ID = "last_action_id"
        private const val KEY_LAST_HABIT_ID = "last_habit_id"

        fun updateAllWidgets(context: Context) {
            val appWidgetManager = AppWidgetManager.getInstance(context.applicationContext)
            val thisWidget = ComponentName(context.applicationContext, TideAppWidgetProvider::class.java)
            val allWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget)
            if (allWidgetIds.isNotEmpty()) {
                val intent = Intent(context.applicationContext, TideAppWidgetProvider::class.java).apply {
                    action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
                    putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, allWidgetIds)
                }
                context.sendBroadcast(intent)
            }
        }
    }

    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action
        if (action == ACTION_LOG_UNIT || action == ACTION_LOG_CRAVING || action == ACTION_UNDO || action == ACTION_NEXT_HABIT) {
            val pendingResult = goAsync()
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    handleQuickAction(context, intent)
                } finally {
                    pendingResult.finish()
                }
            }
            return
        }
        super.onReceive(context, intent)
    }

    private suspend fun handleQuickAction(context: Context, intent: Intent) {
        val database = TideDatabase.getInstance(context)
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

        when (intent.action) {
            ACTION_LOG_UNIT -> {
                val habitId = intent.getLongExtra(EXTRA_HABIT_ID, -1L)
                val habits = database.addictionDao().getActiveAddictionsSync()
                val targetHabit = habits.find { it.id == habitId } ?: habits.firstOrNull()

                if (targetHabit != null) {
                    val product = database.productDao().getProductForAddictionSync(targetHabit.id)
                    val unitCost = product?.unitCost ?: 0.0
                    val entryId = database.consumptionEntryDao().insert(
                        ConsumptionEntryEntity(
                            addictionId = targetHabit.id,
                            timestamp = System.currentTimeMillis(),
                            amount = 1.0,
                            calculatedCost = unitCost,
                            note = "Quick log from home-screen widget"
                        )
                    )
                    prefs.edit()
                        .putString(KEY_LAST_TYPE, "CONSUMPTION")
                        .putLong(KEY_LAST_ID, entryId)
                        .putLong(KEY_LAST_HABIT_ID, targetHabit.id)
                        .apply()
                }
                updateAllWidgetsInternal(context)
            }
            ACTION_LOG_CRAVING -> {
                val habitId = intent.getLongExtra(EXTRA_HABIT_ID, -1L)
                val habits = database.addictionDao().getActiveAddictionsSync()
                val targetHabit = habits.find { it.id == habitId } ?: habits.firstOrNull()

                if (targetHabit != null) {
                    val entryId = database.cravingEntryDao().insert(
                        CravingEntryEntity(
                            addictionId = targetHabit.id,
                            timestamp = System.currentTimeMillis(),
                            intensity = 5,
                            resisted = true,
                            note = "Resisted via widget"
                        )
                    )
                    prefs.edit()
                        .putString(KEY_LAST_TYPE, "CRAVING")
                        .putLong(KEY_LAST_ID, entryId)
                        .putLong(KEY_LAST_HABIT_ID, targetHabit.id)
                        .apply()
                }
                updateAllWidgetsInternal(context)
            }
            ACTION_UNDO -> {
                val lastType = prefs.getString(KEY_LAST_TYPE, null)
                val lastId = prefs.getLong(KEY_LAST_ID, -1L)

                if (lastType != null && lastId > 0) {
                    when (lastType) {
                        "CONSUMPTION" -> database.consumptionEntryDao().deleteById(lastId)
                        "CRAVING" -> database.cravingEntryDao().deleteById(lastId)
                    }
                    prefs.edit().remove(KEY_LAST_TYPE).remove(KEY_LAST_ID).apply()
                }
                updateAllWidgetsInternal(context)
            }
            ACTION_NEXT_HABIT -> {
                val widgetId = intent.getIntExtra(EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID)
                if (widgetId != AppWidgetManager.INVALID_APPWIDGET_ID) {
                    val currentIndex = prefs.getInt("habit_index_$widgetId", 0)
                    prefs.edit().putInt("habit_index_$widgetId", currentIndex + 1).apply()
                }
                updateAllWidgetsInternal(context)
            }
        }
    }

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        val pendingResult = goAsync()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                for (appWidgetId in appWidgetIds) {
                    updateSingleWidget(context, appWidgetManager, appWidgetId)
                }
            } finally {
                pendingResult.finish()
            }
        }
    }

    private suspend fun updateAllWidgetsInternal(context: Context) {
        val appWidgetManager = AppWidgetManager.getInstance(context.applicationContext)
        val thisWidget = ComponentName(context.applicationContext, TideAppWidgetProvider::class.java)
        val allWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget)
        for (appWidgetId in allWidgetIds) {
            updateSingleWidget(context, appWidgetManager, appWidgetId)
        }
    }

    private suspend fun updateSingleWidget(context: Context, appWidgetManager: AppWidgetManager, appWidgetId: Int) {
        val views = RemoteViews(context.packageName, R.layout.widget_layout)
        val database = TideDatabase.getInstance(context)
        val habits = database.addictionDao().getActiveAddictionsSync()
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

        if (habits.isEmpty()) {
            views.setTextViewText(R.id.widget_habit_name, "No Active Habits")
            views.setTextViewText(R.id.widget_progress_text, "Tap to start Tide")
            views.setTextViewText(R.id.widget_subtitle_text, "Track, reduce, or quit any habit")
            views.setViewVisibility(R.id.widget_goal_badge, View.GONE)
            views.setViewVisibility(R.id.widget_btn_next_habit, View.GONE)
            views.setViewVisibility(R.id.widget_btn_log, View.GONE)
            views.setViewVisibility(R.id.widget_btn_craving, View.GONE)
            views.setViewVisibility(R.id.widget_btn_undo, View.GONE)

            val launchIntent = Intent(context, MainActivity::class.java)
            val launchPending = PendingIntent.getActivity(
                context, 0, launchIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            views.setOnClickPendingIntent(R.id.widget_root, launchPending)
            appWidgetManager.updateAppWidget(appWidgetId, views)
            return
        }

        val rawIndex = prefs.getInt("habit_index_$appWidgetId", 0)
        val activeIndex = if (habits.isNotEmpty()) Math.floorMod(rawIndex, habits.size) else 0
        val habit = habits[activeIndex]

        val today = LocalDate.now()
        val startOfDay = TideCalculationEngine.localDateToStartOfDayMillis(today)
        val endOfDay = startOfDay + 86400000L - 1L

        val todayConsumptions = database.consumptionEntryDao().getEntriesForAddictionBetweenSync(habit.id, startOfDay, endOfDay)
        val todayAmount = todayConsumptions.sumOf { it.amount }
        val goal = database.goalDao().getActiveGoalSync(habit.id)
        val target = goal?.targetAmount ?: 0.0

        // Populate info
        views.setTextViewText(R.id.widget_habit_name, habit.name)
        views.setTextViewText(R.id.widget_goal_badge, habit.goalType.uppercase())
        views.setViewVisibility(R.id.widget_goal_badge, View.VISIBLE)
        views.setViewVisibility(R.id.widget_btn_log, View.VISIBLE)
        views.setViewVisibility(R.id.widget_btn_craving, View.VISIBLE)
        views.setViewVisibility(R.id.widget_btn_undo, View.VISIBLE)

        if (habits.size > 1) {
            views.setViewVisibility(R.id.widget_btn_next_habit, View.VISIBLE)
        } else {
            views.setViewVisibility(R.id.widget_btn_next_habit, View.GONE)
        }

        when (habit.goalType.uppercase()) {
            "REDUCE" -> {
                views.setTextViewText(R.id.widget_progress_text, "${todayAmount.toInt()} / ${target.toInt()} ${habit.unit}")
                val left = max(0.0, target - todayAmount)
                views.setTextViewText(R.id.widget_subtitle_text, "Allowance: ${left.toInt()} ${habit.unit} remaining")
            }
            "QUIT" -> {
                val clean = todayAmount == 0.0
                views.setTextViewText(R.id.widget_progress_text, if (clean) "Clean today" else "${todayAmount.toInt()} ${habit.unit} used")
                views.setTextViewText(R.id.widget_subtitle_text, if (clean) "Stay with the tide" else "Slip recorded today")
            }
            else -> { // TRACK
                views.setTextViewText(R.id.widget_progress_text, "${todayAmount.toInt()} ${habit.unit} logged")
                views.setTextViewText(R.id.widget_subtitle_text, "Baseline: ${habit.baselineAmount.toInt()} / ${habit.baselineFrequency}")
            }
        }

        // Action PendingIntents
        val logIntent = Intent(context, TideAppWidgetProvider::class.java).apply {
            action = ACTION_LOG_UNIT
            putExtra(EXTRA_HABIT_ID, habit.id)
            putExtra(EXTRA_APPWIDGET_ID, appWidgetId)
        }
        val logPending = PendingIntent.getBroadcast(
            context, appWidgetId * 10 + 1, logIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        views.setOnClickPendingIntent(R.id.widget_btn_log, logPending)

        val cravingIntent = Intent(context, TideAppWidgetProvider::class.java).apply {
            action = ACTION_LOG_CRAVING
            putExtra(EXTRA_HABIT_ID, habit.id)
            putExtra(EXTRA_APPWIDGET_ID, appWidgetId)
        }
        val cravingPending = PendingIntent.getBroadcast(
            context, appWidgetId * 10 + 2, cravingIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        views.setOnClickPendingIntent(R.id.widget_btn_craving, cravingPending)

        val undoIntent = Intent(context, TideAppWidgetProvider::class.java).apply {
            action = ACTION_UNDO
            putExtra(EXTRA_HABIT_ID, habit.id)
            putExtra(EXTRA_APPWIDGET_ID, appWidgetId)
        }
        val undoPending = PendingIntent.getBroadcast(
            context, appWidgetId * 10 + 3, undoIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        views.setOnClickPendingIntent(R.id.widget_btn_undo, undoPending)

        val nextIntent = Intent(context, TideAppWidgetProvider::class.java).apply {
            action = ACTION_NEXT_HABIT
            putExtra(EXTRA_APPWIDGET_ID, appWidgetId)
        }
        val nextPending = PendingIntent.getBroadcast(
            context, appWidgetId * 10 + 4, nextIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        views.setOnClickPendingIntent(R.id.widget_btn_next_habit, nextPending)

        // Only the header launches the app (Requirement 21: Widget must NOT automatically open the app!)
        val launchIntent = Intent(context, MainActivity::class.java)
        val launchPending = PendingIntent.getActivity(
            context, appWidgetId * 10 + 5, launchIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        views.setOnClickPendingIntent(R.id.widget_header_launch, launchPending)

        appWidgetManager.updateAppWidget(appWidgetId, views)
    }
}
