package com.tide.app.ui.home

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tide.app.core.calculation.AvoidedResult
import com.tide.app.core.calculation.ConsumptionRawData
import com.tide.app.core.calculation.CravingRawData
import com.tide.app.core.calculation.MoneyResult
import com.tide.app.core.calculation.SlipRawData
import com.tide.app.core.calculation.StreakResult
import com.tide.app.core.calculation.TideCalculationEngine
import com.tide.app.core.widget.TideAppWidgetProvider
import com.tide.app.data.local.entity.AddictionEntity
import com.tide.app.data.local.entity.ConsumptionEntryEntity
import com.tide.app.data.local.entity.CravingEntryEntity
import com.tide.app.data.local.entity.DailyCheckInEntity
import com.tide.app.data.local.entity.GoalEntity
import com.tide.app.data.local.entity.ProductEntity
import com.tide.app.data.local.entity.SlipEntryEntity
import com.tide.app.data.local.entity.TriggerEntity
import com.tide.app.data.repository.TideRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import kotlin.math.max

data class HabitCardData(
    val habit: AddictionEntity,
    val product: ProductEntity?,
    val goal: GoalEntity?,
    val streak: StreakResult,
    val money: MoneyResult,
    val avoided: AvoidedResult,
    val todayConsumptionCount: Double,
    val todayConsumptionCost: Double,
    val remainingAllowance: Double,
    val isCleanToday: Boolean
)

data class TodayLogEvent(
    val id: Long,
    val habitId: Long,
    val habitName: String,
    val type: String, // "CONSUMPTION", "CRAVING", "SLIP"
    val title: String,
    val detail: String,
    val timestamp: Long
)

data class UndoBannerState(
    val message: String,
    val entryType: String,
    val entryId: Long,
    val habitId: Long
)

data class HomeUiState(
    val isLoading: Boolean = true,
    val activeHabits: List<HabitCardData> = emptyList(),
    val archivedHabits: List<AddictionEntity> = emptyList(),
    val todayCheckIn: DailyCheckInEntity? = null,
    val triggers: List<TriggerEntity> = emptyList(),
    val todayEvents: List<TodayLogEvent> = emptyList(),
    val undoState: UndoBannerState? = null
) {
    // Convenience properties for single-habit backward compatibility
    val addiction: AddictionEntity? get() = activeHabits.firstOrNull()?.habit
    val product: ProductEntity? get() = activeHabits.firstOrNull()?.product
    val goal: GoalEntity? get() = activeHabits.firstOrNull()?.goal
    val streak: StreakResult get() = activeHabits.firstOrNull()?.streak ?: StreakResult(0, 0, 0, 0, 0.0)
    val money: MoneyResult get() = activeHabits.firstOrNull()?.money ?: MoneyResult(0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0)
    val avoided: AvoidedResult get() = activeHabits.firstOrNull()?.avoided ?: AvoidedResult(0.0, 0.0, 0.0)
    val todayConsumptionCount: Double get() = activeHabits.firstOrNull()?.todayConsumptionCount ?: 0.0
    val todayConsumptionCost: Double get() = activeHabits.firstOrNull()?.todayConsumptionCost ?: 0.0
}

class HomeViewModel(
    private val repository: TideRepository,
    private val appContext: Context? = null
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadHomeData()
    }

    private fun loadHomeData() {
        viewModelScope.launch {
            combine(
                repository.getActiveHabits(),
                repository.getArchivedHabits(),
                repository.getAllTriggers()
            ) { activeHabits, archivedHabits, triggers ->
                Triple(activeHabits, archivedHabits, triggers)
            }.collect { (activeHabits, archivedHabits, triggers) ->
                if (activeHabits.isEmpty()) {
                    _uiState.value = HomeUiState(
                        isLoading = false,
                        activeHabits = emptyList(),
                        archivedHabits = archivedHabits,
                        triggers = triggers
                    )
                    return@collect
                }

                val today = LocalDate.now()
                val startOfDayMillis = TideCalculationEngine.localDateToStartOfDayMillis(today)
                val allEvents = mutableListOf<TodayLogEvent>()
                val cardDataList = mutableListOf<HabitCardData>()

                for (habit in activeHabits) {
                    val product = repository.getProductForAddictionSync(habit.id)
                    val goal = repository.getActiveGoal(habit.id)
                    val consumptions = repository.getConsumptionEntries(habit.id)
                    val cravings = repository.getCravingEntries(habit.id)
                    val slips = repository.getSlipEntries(habit.id)

                    // Synchronously observe current snapshot
                    combine(
                        goal,
                        consumptions,
                        cravings,
                        slips
                    ) { g, cList, crList, sList ->
                        val startDate = TideCalculationEngine.epochMillisToLocalDate(habit.startDate)
                        val effectiveStart = if (startDate.isAfter(today)) today else startDate

                        val consumptionRaw = cList.map {
                            ConsumptionRawData(it.timestamp, it.amount, it.calculatedCost)
                        }
                        val slipRaw = sList.map {
                            SlipRawData(it.timestamp, it.amount, it.cost)
                        }
                        val cravingRaw = crList.map {
                            CravingRawData(it.timestamp, it.intensity, it.resisted)
                        }

                        val dailySummaries = TideCalculationEngine.buildDailySummaries(
                            startDate = effectiveStart,
                            endDate = today,
                            goalType = habit.goalType,
                            targetAmount = g?.targetAmount ?: 0.0,
                            consumptionEntries = consumptionRaw,
                            slipEntries = slipRaw,
                            cravingEntries = cravingRaw
                        )

                        val streak = TideCalculationEngine.calculateStreaks(dailySummaries, today)
                        val money = TideCalculationEngine.calculateMoney(
                            dailySummaries,
                            habit.baselineCost,
                            today,
                            hasFinancialImpact = habit.hasFinancialImpact,
                            goalType = habit.goalType
                        )
                        val avoided = TideCalculationEngine.calculateAvoided(
                            dailySummaries,
                            habit.baselineAmount,
                            today,
                            goalType = habit.goalType
                        )

                        val todayConsumptions = cList.filter { it.timestamp >= startOfDayMillis }
                        val todaySlips = sList.filter { it.timestamp >= startOfDayMillis }
                        val todayCravings = crList.filter { it.timestamp >= startOfDayMillis }

                        val todayAmount = todayConsumptions.sumOf { it.amount }
                        val todayCost = todayConsumptions.sumOf { it.calculatedCost }
                        val target = g?.targetAmount ?: 0.0
                        val remainingAllowance = max(0.0, target - todayAmount)
                        val isCleanToday = when (habit.goalType.uppercase()) {
                            "QUIT" -> todayAmount == 0.0 && todaySlips.isEmpty()
                            "REDUCE" -> todayAmount <= target
                            else -> true
                        }

                        // Collect today's events for this habit
                        todayConsumptions.forEach {
                            allEvents.add(
                                TodayLogEvent(
                                    id = it.id,
                                    habitId = habit.id,
                                    habitName = habit.name,
                                    type = "CONSUMPTION",
                                    title = "Logged ${it.amount.toInt()} ${habit.unit}",
                                    detail = if (habit.hasFinancialImpact) "₹${it.calculatedCost.toInt()}" else habit.name,
                                    timestamp = it.timestamp
                                )
                            )
                        }
                        todaySlips.forEach {
                            allEvents.add(
                                TodayLogEvent(
                                    id = it.id,
                                    habitId = habit.id,
                                    habitName = habit.name,
                                    type = "SLIP",
                                    title = "Slip: ${it.amount.toInt()} ${habit.unit}",
                                    detail = it.mood ?: habit.name,
                                    timestamp = it.timestamp
                                )
                            )
                        }
                        todayCravings.forEach {
                            allEvents.add(
                                TodayLogEvent(
                                    id = it.id,
                                    habitId = habit.id,
                                    habitName = habit.name,
                                    type = "CRAVING",
                                    title = "${habit.name} Craving (${it.intensity}/10)",
                                    detail = if (it.resisted) "Resisted" else "Acted upon",
                                    timestamp = it.timestamp
                                )
                            )
                        }

                        // Update habit-specific milestones
                        repository.checkAndUpdateMilestonesForHabit(
                            addictionId = habit.id,
                            streakDays = streak.currentStreak,
                            totalCravings = crList.size,
                            totalSaved = money.lifetimeSaved,
                            reductionPct = if (habit.baselineAmount > 0) {
                                ((avoided.todayAvoided / habit.baselineAmount) * 100.0)
                            } else 0.0
                        )

                        HabitCardData(
                            habit = habit,
                            product = product,
                            goal = g,
                            streak = streak,
                            money = money,
                            avoided = avoided,
                            todayConsumptionCount = todayAmount,
                            todayConsumptionCost = todayCost,
                            remainingAllowance = remainingAllowance,
                            isCleanToday = isCleanToday
                        )
                    }.collect { cardData ->
                        // Replace or append
                        val existingIndex = cardDataList.indexOfFirst { it.habit.id == cardData.habit.id }
                        if (existingIndex >= 0) {
                            cardDataList[existingIndex] = cardData
                        } else {
                            cardDataList.add(cardData)
                        }
                    }
                }

                allEvents.sortByDescending { it.timestamp }
                val firstHabitId = activeHabits.firstOrNull()?.id ?: 0L
                val todayCheckIn = repository.getDailyCheckInSync(
                    firstHabitId,
                    today.format(DateTimeFormatter.ISO_LOCAL_DATE)
                )

                _uiState.value = HomeUiState(
                    isLoading = false,
                    activeHabits = cardDataList.toList(),
                    archivedHabits = archivedHabits,
                    todayCheckIn = todayCheckIn,
                    triggers = triggers,
                    todayEvents = allEvents.toList(),
                    undoState = _uiState.value.undoState
                )
            }
        }
    }

    fun logConsumption(habitId: Long, amount: Double, triggerId: Long?, note: String?) {
        val habitData = _uiState.value.activeHabits.find { it.habit.id == habitId } ?: return
        val habit = habitData.habit
        val product = habitData.product
        val unitCost = product?.unitCost ?: 0.0
        val cost = if (habit.hasFinancialImpact) amount * unitCost else 0.0

        viewModelScope.launch {
            val entryId = repository.logConsumption(
                ConsumptionEntryEntity(
                    addictionId = habit.id,
                    timestamp = System.currentTimeMillis(),
                    amount = amount,
                    calculatedCost = cost,
                    triggerId = triggerId,
                    note = note
                )
            )

            // Trigger Undo banner
            _uiState.value = _uiState.value.copy(
                undoState = UndoBannerState(
                    message = "Logged ${amount.toInt()} ${habit.unit} for ${habit.name}",
                    entryType = "CONSUMPTION",
                    entryId = entryId,
                    habitId = habit.id
                )
            )

            notifyWidgetUpdate()
        }
    }

    fun logCraving(habitId: Long, intensity: Int, triggerId: Long?, mood: String?, resisted: Boolean, note: String?) {
        val habitData = _uiState.value.activeHabits.find { it.habit.id == habitId } ?: return
        val habit = habitData.habit

        viewModelScope.launch {
            val entryId = repository.logCraving(
                CravingEntryEntity(
                    addictionId = habit.id,
                    timestamp = System.currentTimeMillis(),
                    intensity = intensity,
                    triggerId = triggerId,
                    mood = mood,
                    resisted = resisted,
                    note = note
                )
            )

            _uiState.value = _uiState.value.copy(
                undoState = UndoBannerState(
                    message = "Craving recorded for ${habit.name}",
                    entryType = "CRAVING",
                    entryId = entryId,
                    habitId = habit.id
                )
            )

            notifyWidgetUpdate()
        }
    }

    fun logSlip(habitId: Long, amount: Double, triggerId: Long?, mood: String?, note: String?) {
        val habitData = _uiState.value.activeHabits.find { it.habit.id == habitId } ?: return
        val habit = habitData.habit
        val product = habitData.product
        val unitCost = product?.unitCost ?: 0.0
        val cost = if (habit.hasFinancialImpact) amount * unitCost else 0.0

        viewModelScope.launch {
            val entryId = repository.logSlip(
                SlipEntryEntity(
                    addictionId = habit.id,
                    timestamp = System.currentTimeMillis(),
                    amount = amount,
                    cost = cost,
                    triggerId = triggerId,
                    mood = mood,
                    note = note
                )
            )

            _uiState.value = _uiState.value.copy(
                undoState = UndoBannerState(
                    message = "Slip recorded for ${habit.name}",
                    entryType = "SLIP",
                    entryId = entryId,
                    habitId = habit.id
                )
            )

            notifyWidgetUpdate()
        }
    }

    fun undoLastAction() {
        val banner = _uiState.value.undoState ?: return
        viewModelScope.launch {
            repository.undoEntry(banner.entryType, banner.entryId)
            _uiState.value = _uiState.value.copy(undoState = null)
            notifyWidgetUpdate()
        }
    }

    fun dismissUndo() {
        _uiState.value = _uiState.value.copy(undoState = null)
    }

    fun createHabit(habit: AddictionEntity, product: ProductEntity?, goal: GoalEntity) {
        viewModelScope.launch {
            val defaultTriggers = when (habit.category) {
                "Substance" -> listOf("Stress", "Social situation", "After food", "Boredom", "Routine")
                "Food & Nutrition" -> listOf("Stress", "Cravings", "Boredom", "Fatigue", "Social")
                "Digital & Screen" -> listOf("Boredom", "Procrastination", "Late night", "Stress")
                "Behavioral" -> listOf("Impulse", "Stress", "Boredom", "Social")
                else -> listOf("Stress", "Boredom", "Habit", "Social")
            }
            repository.createHabit(habit, product, goal, defaultTriggers)
            notifyWidgetUpdate()
        }
    }

    fun updateHabit(habit: AddictionEntity, product: ProductEntity?, goal: GoalEntity?) {
        viewModelScope.launch {
            repository.updateHabitConfig(habit, product, goal)
            notifyWidgetUpdate()
        }
    }

    fun archiveHabit(habitId: Long) {
        viewModelScope.launch {
            repository.archiveHabit(habitId)
            notifyWidgetUpdate()
        }
    }

    fun restoreHabit(habitId: Long) {
        viewModelScope.launch {
            repository.restoreHabit(habitId)
            notifyWidgetUpdate()
        }
    }

    fun deleteHabit(habitId: Long) {
        viewModelScope.launch {
            repository.deleteHabit(habitId)
            notifyWidgetUpdate()
        }
    }

    fun saveCheckIn(mood: String, cravingOccurred: Boolean, intensity: Int, note: String?) {
        val firstHabitId = _uiState.value.activeHabits.firstOrNull()?.habit?.id ?: return
        val todayStr = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE)

        viewModelScope.launch {
            repository.saveDailyCheckIn(
                DailyCheckInEntity(
                    addictionId = firstHabitId,
                    date = todayStr,
                    mood = mood,
                    cravingOccurred = cravingOccurred,
                    cravingIntensity = intensity,
                    note = note
                )
            )
        }
    }

    private fun notifyWidgetUpdate() {
        appContext?.let { TideAppWidgetProvider.updateAllWidgets(it) }
    }
}
