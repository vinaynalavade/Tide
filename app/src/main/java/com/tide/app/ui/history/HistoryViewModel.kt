package com.tide.app.ui.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tide.app.core.calculation.ConsumptionRawData
import com.tide.app.core.calculation.CravingRawData
import com.tide.app.core.calculation.DailySummary
import com.tide.app.core.calculation.SlipRawData
import com.tide.app.core.calculation.TideCalculationEngine
import com.tide.app.data.local.entity.AddictionEntity
import com.tide.app.data.local.entity.ConsumptionEntryEntity
import com.tide.app.data.local.entity.CravingEntryEntity
import com.tide.app.data.local.entity.DailyCheckInEntity
import com.tide.app.data.local.entity.GoalEntity
import com.tide.app.data.local.entity.ProductEntity
import com.tide.app.data.local.entity.SlipEntryEntity
import com.tide.app.data.repository.TideRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter

data class HistoryDayDetail(
    val date: LocalDate,
    val consumptionAmount: Double,
    val targetAmount: Double,
    val unit: String,
    val cravingsCount: Int,
    val cravingsResistedCount: Int,
    val moneySpent: Double,
    val moneySaved: Double,
    val mood: String?,
    val isClean: Boolean,
    val hasSlip: Boolean,
    val consumptions: List<ConsumptionEntryEntity>,
    val cravings: List<CravingEntryEntity>,
    val slips: List<SlipEntryEntity>
)

data class HistoryUiState(
    val isLoading: Boolean = true,
    val addiction: AddictionEntity? = null,
    val product: ProductEntity? = null,
    val goal: GoalEntity? = null,
    val selectedMonth: YearMonth = YearMonth.now(),
    val dailySummaries: Map<LocalDate, DailySummary> = emptyMap(),
    val selectedDayDetail: HistoryDayDetail? = null
)

class HistoryViewModel(
    private val repository: TideRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HistoryUiState())
    val uiState: StateFlow<HistoryUiState> = _uiState.asStateFlow()

    private var allConsumptions = listOf<ConsumptionEntryEntity>()
    private var allCravings = listOf<CravingEntryEntity>()
    private var allSlips = listOf<SlipEntryEntity>()
    private var allCheckIns = listOf<DailyCheckInEntity>()

    init {
        loadHistory()
    }

    private fun loadHistory() {
        viewModelScope.launch {
            repository.getActiveAddiction().collect { addiction ->
                if (addiction == null) {
                    _uiState.value = HistoryUiState(isLoading = false)
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
                    repository.getAllDailyCheckIns(addiction.id)
                ) { (product, goal), consumptions, cravings, slips, checkIns ->
                    allConsumptions = consumptions
                    allCravings = cravings
                    allSlips = slips
                    allCheckIns = checkIns

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

                    HistoryUiState(
                        isLoading = false,
                        addiction = addiction,
                        product = product,
                        goal = goal,
                        selectedMonth = _uiState.value.selectedMonth,
                        dailySummaries = summaries,
                        selectedDayDetail = _uiState.value.selectedDayDetail
                    )
                }.collect { state ->
                    _uiState.value = state
                }
            }
        }
    }

    fun changeMonth(monthOffset: Long) {
        val nextMonth = _uiState.value.selectedMonth.plusMonths(monthOffset)
        _uiState.value = _uiState.value.copy(selectedMonth = nextMonth)
    }

    fun selectDate(date: LocalDate) {
        val s = _uiState.value
        val addiction = s.addiction ?: return
        val summary = s.dailySummaries[date] ?: DailySummary(date = date)

        val startOfDayMillis = TideCalculationEngine.localDateToStartOfDayMillis(date)
        val endOfDayMillis = startOfDayMillis + (24 * 60 * 60 * 1000L) - 1

        val dayConsumptions = allConsumptions.filter { it.timestamp in startOfDayMillis..endOfDayMillis }
        val dayCravings = allCravings.filter { it.timestamp in startOfDayMillis..endOfDayMillis }
        val daySlips = allSlips.filter { it.timestamp in startOfDayMillis..endOfDayMillis }
        val dateStr = date.format(DateTimeFormatter.ISO_LOCAL_DATE)
        val dayCheckIn = allCheckIns.find { it.date == dateStr }

        val spent = summary.consumptionCost + summary.slipCost
        val saved = kotlin.math.max(0.0, addiction.baselineCost - spent)

        _uiState.value = s.copy(
            selectedDayDetail = HistoryDayDetail(
                date = date,
                consumptionAmount = summary.consumptionAmount,
                targetAmount = s.goal?.targetAmount ?: 0.0,
                unit = addiction.unit,
                cravingsCount = summary.cravingsCount,
                cravingsResistedCount = summary.cravingsResistedCount,
                moneySpent = spent,
                moneySaved = saved,
                mood = dayCheckIn?.mood,
                isClean = summary.isClean,
                hasSlip = summary.slipAmount > 0,
                consumptions = dayConsumptions,
                cravings = dayCravings,
                slips = daySlips
            )
        )
    }

    fun clearSelectedDay() {
        _uiState.value = _uiState.value.copy(selectedDayDetail = null)
    }
}
