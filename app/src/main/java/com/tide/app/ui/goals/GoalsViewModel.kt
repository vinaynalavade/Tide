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
    val addiction: AddictionEntity? = null,
    val activeGoal: GoalEntity? = null,
    val streak: StreakResult = StreakResult(0, 0, 0, 0, 0.0),
    val money: MoneyResult = MoneyResult(0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0),
    val milestones: List<MilestoneEntity> = emptyList(),
    val recoveryGoals: List<RecoveryGoalEntity> = emptyList()
)

class GoalsViewModel(
    private val repository: TideRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(GoalsUiState())
    val uiState: StateFlow<GoalsUiState> = _uiState.asStateFlow()

    init {
        loadGoalsData()
    }

    private fun loadGoalsData() {
        viewModelScope.launch {
            repository.getActiveAddiction().collect { addiction ->
                if (addiction == null) {
                    _uiState.value = GoalsUiState(isLoading = false)
                    return@collect
                }

                combine(
                    repository.getActiveGoal(addiction.id),
                    combine(
                        repository.getMilestones(),
                        repository.getRecoveryGoals()
                    ) { m, r -> Pair(m, r) },
                    repository.getConsumptionEntries(addiction.id),
                    repository.getCravingEntries(addiction.id),
                    repository.getSlipEntries(addiction.id)
                ) { goal, (milestones, recoveryGoals), consumptions, cravings, slips ->
                    val today = LocalDate.now()
                    val startDate = TideCalculationEngine.epochMillisToLocalDate(addiction.startDate)
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
                        goalType = addiction.goalType,
                        targetAmount = goal?.targetAmount ?: 0.0,
                        consumptionEntries = consumptionRaw,
                        slipEntries = slipRaw,
                        cravingEntries = cravingRaw
                    )

                    val streak = TideCalculationEngine.calculateStreaks(summaries, today)
                    val money = TideCalculationEngine.calculateMoney(summaries, addiction.baselineCost, today)

                    GoalsUiState(
                        isLoading = false,
                        addiction = addiction,
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
