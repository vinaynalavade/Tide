package com.tide.app.ui.insights

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tide.app.core.designsystem.ChartBarData
import com.tide.app.core.calculation.ConsumptionRawData
import com.tide.app.core.calculation.CravingInsight
import com.tide.app.core.calculation.CravingRawData
import com.tide.app.core.calculation.DailySummary
import com.tide.app.core.calculation.MoneyResult
import com.tide.app.core.calculation.ProgressInsight
import com.tide.app.core.calculation.SlipRawData
import com.tide.app.core.calculation.TideCalculationEngine
import com.tide.app.core.calculation.TriggerInsight
import com.tide.app.data.local.entity.AddictionEntity
import com.tide.app.data.local.entity.TriggerEntity
import com.tide.app.data.repository.TideRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter

enum class TrendTimeframe {
    LAST_7_DAYS,
    LAST_30_DAYS
}

data class InsightsUiState(
    val isLoading: Boolean = true,
    val addiction: AddictionEntity? = null,
    val timeframe: TrendTimeframe = TrendTimeframe.LAST_7_DAYS,
    val consumptionChartData: List<ChartBarData> = emptyList(),
    val money: MoneyResult = MoneyResult(0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0),
    val cravingInsight: CravingInsight = CravingInsight(0, 0, 0.0, 0.0),
    val triggersInsight: List<TriggerInsight> = emptyList(),
    val progressInsight: ProgressInsight = ProgressInsight(0.0, 0.0, 0.0, false, ""),
    val totalCleanDays: Int = 0,
    val totalDaysTracked: Int = 0
)

class InsightsViewModel(
    private val repository: TideRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(InsightsUiState())
    val uiState: StateFlow<InsightsUiState> = _uiState.asStateFlow()

    private var cachedSummaries: Map<LocalDate, DailySummary> = emptyMap()
    private var cachedTriggers: List<TriggerEntity> = emptyList()

    init {
        loadInsights()
    }

    private fun loadInsights() {
        viewModelScope.launch {
            repository.getActiveAddiction().collect { addiction ->
                if (addiction == null) {
                    _uiState.value = InsightsUiState(isLoading = false)
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
                    cachedTriggers = triggers
                    val today = LocalDate.now()
                    val startDate = TideCalculationEngine.epochMillisToLocalDate(addiction.startDate)
                    val effectiveStart = if (startDate.isAfter(today)) today else startDate

                    val consumptionRaw = consumptions.map {
                        val triggerName = triggers.find { t -> t.id == it.triggerId }?.name
                        ConsumptionRawData(it.timestamp, it.amount, it.calculatedCost, triggerName)
                    }
                    val slipRaw = slips.map {
                        val triggerName = triggers.find { t -> t.id == it.triggerId }?.name
                        SlipRawData(it.timestamp, it.amount, it.cost, triggerName)
                    }
                    val cravingRaw = cravings.map {
                        val triggerName = triggers.find { t -> t.id == it.triggerId }?.name
                        CravingRawData(it.timestamp, it.intensity, it.resisted, triggerName)
                    }

                    cachedSummaries = TideCalculationEngine.buildDailySummaries(
                        startDate = effectiveStart,
                        endDate = today,
                        goalType = addiction.goalType,
                        targetAmount = goal?.targetAmount ?: 0.0,
                        consumptionEntries = consumptionRaw,
                        slipEntries = slipRaw,
                        cravingEntries = cravingRaw
                    )

                    val money = TideCalculationEngine.calculateMoney(cachedSummaries, addiction.baselineCost, today)
                    val cravingInsight = TideCalculationEngine.calculateCravingInsights(cravingRaw)

                    // Collect all recorded trigger names
                    val triggerOccurrences = mutableListOf<String>()
                    consumptionRaw.forEach { it.triggerName?.let { name -> triggerOccurrences.add(name) } }
                    slipRaw.forEach { it.triggerName?.let { name -> triggerOccurrences.add(name) } }
                    cravingRaw.forEach { it.triggerName?.let { name -> triggerOccurrences.add(name) } }

                    val triggersInsight = TideCalculationEngine.calculateTriggerInsights(triggerOccurrences)
                    val progressInsight = TideCalculationEngine.calculateProgressInsight(cachedSummaries, addiction.baselineAmount)

                    val totalClean = cachedSummaries.values.count { it.isClean }

                    val chartData = buildChartData(cachedSummaries, _uiState.value.timeframe, today)

                    InsightsUiState(
                        isLoading = false,
                        addiction = addiction,
                        timeframe = _uiState.value.timeframe,
                        consumptionChartData = chartData,
                        money = money,
                        cravingInsight = cravingInsight,
                        triggersInsight = triggersInsight,
                        progressInsight = progressInsight,
                        totalCleanDays = totalClean,
                        totalDaysTracked = cachedSummaries.size
                    )
                }.collect { state ->
                    _uiState.value = state
                }
            }
        }
    }

    fun setTimeframe(timeframe: TrendTimeframe) {
        val today = LocalDate.now()
        val chartData = buildChartData(cachedSummaries, timeframe, today)
        _uiState.value = _uiState.value.copy(
            timeframe = timeframe,
            consumptionChartData = chartData
        )
    }

    private fun buildChartData(
        summaries: Map<LocalDate, DailySummary>,
        timeframe: TrendTimeframe,
        today: LocalDate
    ): List<ChartBarData> {
        val daysCount = if (timeframe == TrendTimeframe.LAST_7_DAYS) 7 else 30
        val list = mutableListOf<ChartBarData>()

        for (i in (daysCount - 1) downTo 0) {
            val date = today.minusDays(i.toLong())
            val summary = summaries[date]
            val totalUse = (summary?.consumptionAmount ?: 0.0) + (summary?.slipAmount ?: 0.0)
            val label = if (daysCount == 7) {
                date.format(DateTimeFormatter.ofPattern("EEE"))
            } else {
                "${date.dayOfMonth}"
            }
            list.add(ChartBarData(label = label, value = totalUse))
        }

        return list
    }
}
