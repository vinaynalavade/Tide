package com.tide.app.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tide.app.core.calculation.AvoidedResult
import com.tide.app.core.calculation.ConsumptionRawData
import com.tide.app.core.calculation.CravingRawData
import com.tide.app.core.calculation.MoneyResult
import com.tide.app.core.calculation.SlipRawData
import com.tide.app.core.calculation.StreakResult
import com.tide.app.core.calculation.TideCalculationEngine
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
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

data class HomeUiState(
    val isLoading: Boolean = true,
    val addiction: AddictionEntity? = null,
    val product: ProductEntity? = null,
    val goal: GoalEntity? = null,
    val streak: StreakResult = StreakResult(0, 0, 0, 0, 0.0),
    val money: MoneyResult = MoneyResult(0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0),
    val avoided: AvoidedResult = AvoidedResult(0.0, 0.0, 0.0),
    val todayCheckIn: DailyCheckInEntity? = null,
    val todayConsumptionCount: Double = 0.0,
    val todayConsumptionCost: Double = 0.0,
    val triggers: List<TriggerEntity> = emptyList(),
    val todayEvents: List<TodayLogEvent> = emptyList()
)

data class TodayLogEvent(
    val id: Long,
    val type: String, // "CONSUMPTION", "CRAVING", "SLIP"
    val title: String,
    val detail: String,
    val timestamp: Long
)

class HomeViewModel(
    private val repository: TideRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadHomeData()
    }

    private fun loadHomeData() {
        viewModelScope.launch {
            repository.getActiveAddiction().collect { addiction ->
                if (addiction == null) {
                    _uiState.value = HomeUiState(isLoading = false)
                    return@collect
                }

                combine(
                    combine(
                        repository.getProductForAddiction(addiction.id),
                        repository.getActiveGoal(addiction.id)
                    ) { p, g -> Pair(p, g) },
                    repository.getConsumptionEntries(addiction.id),
                    repository.getCravingEntries(addiction.id),
                    repository.getSlipEntries(addiction.id),
                    repository.getAllTriggers()
                ) { (product, goal), consumptions, cravings, slips, triggers ->
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

                    val dailySummaries = TideCalculationEngine.buildDailySummaries(
                        startDate = effectiveStart,
                        endDate = today,
                        goalType = addiction.goalType,
                        targetAmount = goal?.targetAmount ?: 0.0,
                        consumptionEntries = consumptionRaw,
                        slipEntries = slipRaw,
                        cravingEntries = cravingRaw
                    )

                    val streak = TideCalculationEngine.calculateStreaks(dailySummaries, today)
                    val money = TideCalculationEngine.calculateMoney(dailySummaries, addiction.baselineCost, today)
                    val avoided = TideCalculationEngine.calculateAvoided(dailySummaries, addiction.baselineAmount, today)

                    // Check milestones progress
                    repository.checkAndUpdateMilestones(
                        streakDays = streak.currentStreak,
                        totalCravings = cravings.size,
                        totalSaved = money.lifetimeSaved,
                        reductionPct = if (addiction.baselineAmount > 0) {
                            ((avoided.todayAvoided / addiction.baselineAmount) * 100.0)
                        } else 0.0
                    )

                    // Compute today's events
                    val startOfDayMillis = TideCalculationEngine.localDateToStartOfDayMillis(today)
                    val todayConsumptions = consumptions.filter { it.timestamp >= startOfDayMillis }
                    val todaySlips = slips.filter { it.timestamp >= startOfDayMillis }
                    val todayCravings = cravings.filter { it.timestamp >= startOfDayMillis }

                    val events = mutableListOf<TodayLogEvent>()
                    todayConsumptions.forEach {
                        events.add(TodayLogEvent(it.id, "CONSUMPTION", "Used ${it.amount.toInt()} ${addiction.unit}", "₹${it.calculatedCost.toInt()}", it.timestamp))
                    }
                    todaySlips.forEach {
                        events.add(TodayLogEvent(it.id, "SLIP", "Slip: ${it.amount.toInt()} ${addiction.unit}", it.mood ?: "Recorded", it.timestamp))
                    }
                    todayCravings.forEach {
                        events.add(TodayLogEvent(it.id, "CRAVING", "Craving (Intensity ${it.intensity}/10)", if (it.resisted) "Resisted" else "Acted upon", it.timestamp))
                    }
                    events.sortByDescending { it.timestamp }

                    val todayCheckIn = repository.getDailyCheckInSync(
                        addiction.id,
                        today.format(DateTimeFormatter.ISO_LOCAL_DATE)
                    )

                    HomeUiState(
                        isLoading = false,
                        addiction = addiction,
                        product = product,
                        goal = goal,
                        streak = streak,
                        money = money,
                        avoided = avoided,
                        todayCheckIn = todayCheckIn,
                        todayConsumptionCount = todayConsumptions.sumOf { it.amount },
                        todayConsumptionCost = todayConsumptions.sumOf { it.calculatedCost },
                        triggers = triggers,
                        todayEvents = events
                    )
                }.collect { state ->
                    _uiState.value = state
                }
            }
        }
    }

    fun logConsumption(amount: Double, triggerId: Long?, note: String?) {
        val s = _uiState.value
        val addiction = s.addiction ?: return
        val product = s.product
        val unitCost = product?.unitCost ?: 0.0
        val cost = amount * unitCost

        viewModelScope.launch {
            repository.logConsumption(
                ConsumptionEntryEntity(
                    addictionId = addiction.id,
                    timestamp = System.currentTimeMillis(),
                    amount = amount,
                    calculatedCost = cost,
                    triggerId = triggerId,
                    note = note
                )
            )
        }
    }

    fun logCraving(intensity: Int, triggerId: Long?, mood: String?, resisted: Boolean, note: String?) {
        val addiction = _uiState.value.addiction ?: return
        viewModelScope.launch {
            repository.logCraving(
                CravingEntryEntity(
                    addictionId = addiction.id,
                    timestamp = System.currentTimeMillis(),
                    intensity = intensity,
                    triggerId = triggerId,
                    mood = mood,
                    resisted = resisted,
                    note = note
                )
            )
        }
    }

    fun logSlip(amount: Double, triggerId: Long?, mood: String?, note: String?) {
        val s = _uiState.value
        val addiction = s.addiction ?: return
        val product = s.product
        val unitCost = product?.unitCost ?: 0.0
        val cost = amount * unitCost

        viewModelScope.launch {
            repository.logSlip(
                SlipEntryEntity(
                    addictionId = addiction.id,
                    timestamp = System.currentTimeMillis(),
                    amount = amount,
                    cost = cost,
                    triggerId = triggerId,
                    mood = mood,
                    note = note
                )
            )
        }
    }

    fun saveCheckIn(mood: String, cravingOccurred: Boolean, intensity: Int, note: String?) {
        val addiction = _uiState.value.addiction ?: return
        val todayStr = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE)

        viewModelScope.launch {
            repository.saveDailyCheckIn(
                DailyCheckInEntity(
                    addictionId = addiction.id,
                    date = todayStr,
                    mood = mood,
                    cravingOccurred = cravingOccurred,
                    cravingIntensity = intensity,
                    note = note
                )
            )
        }
    }
}
