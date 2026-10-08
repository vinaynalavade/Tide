package com.tide.app.ui.goals

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tide.app.core.calculation.ConsumptionRawData
import com.tide.app.core.calculation.CravingRawData
import com.tide.app.core.calculation.DailySummary
import com.tide.app.core.calculation.MoneyResult
import com.tide.app.core.calculation.SlipRawData
import com.tide.app.core.calculation.StreakResult
import com.tide.app.core.calculation.TideCalculationEngine
import com.tide.app.data.local.entity.AddictionEntity
import com.tide.app.data.local.entity.GoalEntity
import com.tide.app.data.local.entity.MilestoneEntity
import com.tide.app.data.local.entity.RecoveryGoalEntity
import com.tide.app.data.repository.TideRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import java.time.LocalDate

data class GoalsUiState(
    val isLoading: Boolean = true,
    val activeHabits: List<AddictionEntity> = emptyList(),
    val selectedHabit: AddictionEntity? = null,
    val activeGoal: GoalEntity? = null,
    val streak: StreakResult = StreakResult(0, 0, 0, 0, 0.0),
    val money: MoneyResult = MoneyResult(0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0),
    val milestones: List<MilestoneEntity> = emptyList(),
    val recoveryGoals: List<RecoveryGoalEntity> = emptyList()
) {
    // Backward-compatibility alias
    val addiction: AddictionEntity? get() = selectedHabit
}

class GoalsViewModel(
    private val repository: TideRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(GoalsUiState())
    val uiState: StateFlow<GoalsUiState> = _uiState.asStateFlow()

    private var selectedHabitIdOverride: Long? = null

    init {
        loadGoalsData()
    }

    private fun loadGoalsData() {
        viewModelScope.launch {
            repository.getActiveHabits().collect { activeHabits ->
                if (activeHabits.isEmpty()) {
                    _uiState.value = GoalsUiState(isLoading = false)
                    return@collect
                }

                val habit = activeHabits.find { it.id == selectedHabitIdOverride }
                    ?: activeHabits.first()

                combine(
                    repository.getActiveGoal(habit.id),
                    combine(
                        repository.getMilestonesForHabit(habit.id),
                        repository.getRecoveryGoals()
                    ) { m, r -> Pair(m, r) },
                    repository.getConsumptionEntries(habit.id),
                    repository.getCravingEntries(habit.id),
                    repository.getSlipEntries(habit.id)
                ) { goal, (milestones, recoveryGoals), consumptions, cravings, slips ->
                    val today = LocalDate.now()
                    val startDate = TideCalculationEngine.epochMillisToLocalDate(habit.startDate)
                    val effectiveStart = if (startDate.isAfter(today)) today else startDate

                    val consumptionRaw = consumptions.map {
                        ConsumptionRawData(it.timestamp, it.amount, it.calculatedCost)
                    }
                    val slipRaw = slips.map {
                        SlipRawData(it.timestamp, it.amount, it.cost)
                    }
                    val cravingRaw = cravings.map {
                        CravingRawData(it.timestamp, it.intensity, it.resisted)
                    }

                    val summaries = TideCalculationEngine.buildDailySummaries(
                        startDate = effectiveStart,
                        endDate = today,
                        goalType = habit.goalType,
                        targetAmount = goal?.targetAmount ?: 0.0,
                        consumptionEntries = consumptionRaw,
                        slipEntries = slipRaw,
                        cravingEntries = cravingRaw
                    )

                    val streak = TideCalculationEngine.calculateStreaks(summaries, today)
                    val money = TideCalculationEngine.calculateMoney(
                        summaries,
                        habit.baselineCost,
                        today,
                        hasFinancialImpact = habit.hasFinancialImpact,
                        goalType = habit.goalType
                    )

                    GoalsUiState(
                        isLoading = false,
                        activeHabits = activeHabits,
                        selectedHabit = habit,
                        activeGoal = goal,
                        streak = streak,
                        money = money,
                        milestones = milestones,
                        recoveryGoals = recoveryGoals
                    )
                }.collect { state ->
                    _uiState.value = state
                }
            }
        }
    }

    fun selectHabit(habitId: Long) {
        selectedHabitIdOverride = habitId
        loadGoalsData()
    }

    fun changeGoal(habitId: Long, newGoalType: String, newTargetAmount: Double) {
        viewModelScope.launch {
            val habit = repository.getHabitByIdSync(habitId) ?: return@launch
            val updatedHabit = habit.copy(goalType = newGoalType.uppercase())
            val existingGoal = repository.getActiveGoal(habitId) // Flow
            val goal = GoalEntity(
                addictionId = habitId,
                type = newGoalType.uppercase(),
                targetAmount = if (newGoalType.uppercase() == "REDUCE") newTargetAmount else 0.0,
                targetFrequency = "day",
                active = true
            )
            repository.updateHabitConfig(updatedHabit, null, goal)
        }
    }

    fun addRecoveryGoal(name: String, targetAmount: Double) {
        viewModelScope.launch {
            repository.addRecoveryGoal(name, targetAmount)
        }
    }

    fun deleteRecoveryGoal(goal: RecoveryGoalEntity) {
        viewModelScope.launch {
            repository.deleteRecoveryGoal(goal)
        }
    }
}
