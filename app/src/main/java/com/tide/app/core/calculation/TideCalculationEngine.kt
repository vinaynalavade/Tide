package com.tide.app.core.calculation

import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.temporal.ChronoUnit
import kotlin.math.max
import kotlin.math.roundToInt

data class DailySummary(
    val date: LocalDate,
    val consumptionAmount: Double = 0.0,
    val consumptionCost: Double = 0.0,
    val slipAmount: Double = 0.0,
    val slipCost: Double = 0.0,
    val cravingsCount: Int = 0,
    val cravingsResistedCount: Int = 0,
    val totalCravingsIntensity: Int = 0,
    val isClean: Boolean = true
)

data class StreakResult(
    val currentStreak: Int,
    val longestStreak: Int,
    val totalCleanDays: Int,
    val totalElapsedDays: Int,
    val successRate: Double // 0.0 to 100.0
)

data class MoneyResult(
    val todaySpent: Double,
    val todaySaved: Double,
    val last7DaysSpent: Double,
    val last7DaysSaved: Double,
    val last30DaysSpent: Double,
    val last30DaysSaved: Double,
    val lifetimeSpent: Double,
    val lifetimeSaved: Double,
    val yearlyProjectedSaved: Double
)

data class AvoidedResult(
    val todayAvoided: Double,
    val last7DaysAvoided: Double,
    val lifetimeAvoided: Double
)

data class TimeResult(
    val todayMinutesSpent: Long,
    val todayMinutesSaved: Long,
    val lifetimeMinutesSpent: Long,
    val lifetimeMinutesSaved: Long
)

data class TriggerInsight(
    val name: String,
    val count: Int,
    val percentage: Double
)

data class CravingInsight(
    val totalCravings: Int,
    val resistedCount: Int,
    val resistedPercentage: Double,
    val averageIntensity: Double
)

data class ProgressInsight(
    val reductionPercentage: Double,
    val baselineDailyAmount: Double,
    val currentDailyAverage: Double,
    val hasSufficientData: Boolean,
    val message: String
)

object TideCalculationEngine {

    fun epochMillisToLocalDate(epochMillis: Long, zoneId: ZoneId = ZoneId.systemDefault()): LocalDate {
        return Instant.ofEpochMilli(epochMillis).atZone(zoneId).toLocalDate()
    }

    fun localDateToStartOfDayMillis(date: LocalDate, zoneId: ZoneId = ZoneId.systemDefault()): Long {
        return date.atStartOfDay(zoneId).toInstant().toEpochMilli()
    }

    /**
     * Aggregates raw entries into daily summaries from startDate up to today
     */
    fun buildDailySummaries(
        startDate: LocalDate,
        endDate: LocalDate,
        goalType: String, // "QUIT", "REDUCE", "TRACK"
        targetAmount: Double,
        consumptionEntries: List<ConsumptionRawData>,
        slipEntries: List<SlipRawData>,
        cravingEntries: List<CravingRawData>,
        zoneId: ZoneId = ZoneId.systemDefault()
    ): Map<LocalDate, DailySummary> {
        val daysBetween = ChronoUnit.DAYS.between(startDate, endDate)
        val validDays = max(0L, daysBetween)
        val map = mutableMapOf<LocalDate, DailySummary>()

        for (i in 0..validDays) {
            val date = startDate.plusDays(i)
            map[date] = DailySummary(date = date)
        }

        // Map consumption
        for (c in consumptionEntries) {
            val date = epochMillisToLocalDate(c.timestamp, zoneId)
            if (map.containsKey(date)) {
                val current = map[date]!!
                map[date] = current.copy(
                    consumptionAmount = current.consumptionAmount + c.amount,
                    consumptionCost = current.consumptionCost + c.calculatedCost
                )
            }
        }

        // Map slips
        for (s in slipEntries) {
            val date = epochMillisToLocalDate(s.timestamp, zoneId)
            if (map.containsKey(date)) {
                val current = map[date]!!
                map[date] = current.copy(
                    slipAmount = current.slipAmount + s.amount,
                    slipCost = current.slipCost + s.cost
                )
            }
        }

        // Map cravings
        for (cr in cravingEntries) {
            val date = epochMillisToLocalDate(cr.timestamp, zoneId)
            if (map.containsKey(date)) {
                val current = map[date]!!
                map[date] = current.copy(
                    cravingsCount = current.cravingsCount + 1,
                    cravingsResistedCount = current.cravingsResistedCount + if (cr.resisted) 1 else 0,
                    totalCravingsIntensity = current.totalCravingsIntensity + cr.intensity
                )
            }
        }

        // Calculate isClean for each day based on goalType
        for ((date, summary) in map.entries) {
            val totalConsumed = summary.consumptionAmount + summary.slipAmount
            val isClean = when (goalType.uppercase()) {
                "QUIT" -> totalConsumed == 0.0 && summary.slipAmount == 0.0
                "REDUCE" -> totalConsumed <= targetAmount
                "TRACK" -> true // Track mode has no fail state
                else -> totalConsumed == 0.0
            }
            map[date] = summary.copy(isClean = isClean)
        }

        return map
    }

    /**
     * Calculates streak statistics
     */
    fun calculateStreaks(
        dailySummaries: Map<LocalDate, DailySummary>,
        today: LocalDate
    ): StreakResult {
        if (dailySummaries.isEmpty()) {
            return StreakResult(0, 0, 0, 0, 0.0)
        }

        val sortedDates = dailySummaries.keys.sorted()
        var longestStreak = 0
        var runningStreak = 0
        var totalCleanDays = 0

        for (date in sortedDates) {
            val summary = dailySummaries[date] ?: continue
            if (summary.isClean) {
                runningStreak++
                totalCleanDays++
                if (runningStreak > longestStreak) {
                    longestStreak = runningStreak
                }
            } else {
                runningStreak = 0
            }
        }

        // Calculate current streak walking backwards from today
        var currentStreak = 0
        var checkDate = today
        while (dailySummaries.containsKey(checkDate)) {
            val summary = dailySummaries[checkDate]!!
            if (summary.isClean) {
                currentStreak++
                checkDate = checkDate.minusDays(1)
            } else {
                break
            }
        }

        val totalDays = sortedDates.size
        val successRate = if (totalDays > 0) {
            (totalCleanDays.toDouble() / totalDays.toDouble()) * 100.0
        } else 0.0

        return StreakResult(
            currentStreak = currentStreak,
            longestStreak = max(longestStreak, currentStreak),
            totalCleanDays = totalCleanDays,
            totalElapsedDays = totalDays,
            successRate = ((successRate * 10.0).roundToInt() / 10.0)
        )
    }

    /**
     * Pure calculation of Money Spent and Saved
     * Only calculates savings if hasFinancialImpact is true and not in TRACK mode.
     */
    fun calculateMoney(
        dailySummaries: Map<LocalDate, DailySummary>,
        baselineDailyCost: Double,
        today: LocalDate,
        hasFinancialImpact: Boolean = true,
        goalType: String = "QUIT"
    ): MoneyResult {
        if (!hasFinancialImpact || dailySummaries.isEmpty()) {
            return MoneyResult(0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0)
        }

        val isTrack = goalType.equals("TRACK", ignoreCase = true)
        var lifetimeSpent = 0.0
        var lifetimeSaved = 0.0

        var todaySpent = 0.0
        var todaySaved = 0.0

        var last7Spent = 0.0
        var last7Saved = 0.0

        var last30Spent = 0.0
        var last30Saved = 0.0

        val d7Threshold = today.minusDays(6)
        val d30Threshold = today.minusDays(29)

        for ((date, summary) in dailySummaries) {
            val daySpent = summary.consumptionCost + summary.slipCost
            val daySaved = if (isTrack) 0.0 else max(0.0, baselineDailyCost - daySpent)

            lifetimeSpent += daySpent
            lifetimeSaved += daySaved

            if (date == today) {
                todaySpent += daySpent
                todaySaved += daySaved
            }
            if (!date.isBefore(d7Threshold) && !date.isAfter(today)) {
                last7Spent += daySpent
                last7Saved += daySaved
            }
            if (!date.isBefore(d30Threshold) && !date.isAfter(today)) {
                last30Spent += daySpent
                last30Saved += daySaved
            }
        }

        val elapsedDays = max(1, dailySummaries.size)
        val avgDailySaved = if (isTrack) 0.0 else lifetimeSaved / elapsedDays
        val yearlyProjected = avgDailySaved * 365.0

        return MoneyResult(
            todaySpent = roundToTwoDecimals(todaySpent),
            todaySaved = roundToTwoDecimals(todaySaved),
            last7DaysSpent = roundToTwoDecimals(last7Spent),
            last7DaysSaved = roundToTwoDecimals(last7Saved),
            last30DaysSpent = roundToTwoDecimals(last30Spent),
            last30DaysSaved = roundToTwoDecimals(last30Saved),
            lifetimeSpent = roundToTwoDecimals(lifetimeSpent),
            lifetimeSaved = roundToTwoDecimals(lifetimeSaved),
            yearlyProjectedSaved = roundToTwoDecimals(yearlyProjected)
        )
    }

    /**
     * Avoided consumption calculation
     */
    fun calculateAvoided(
        dailySummaries: Map<LocalDate, DailySummary>,
        baselineDailyAmount: Double,
        today: LocalDate,
        goalType: String = "QUIT"
    ): AvoidedResult {
        if (goalType.equals("TRACK", ignoreCase = true) || dailySummaries.isEmpty()) {
            return AvoidedResult(0.0, 0.0, 0.0)
        }

        var lifetimeAvoided = 0.0
        var todayAvoided = 0.0
        var last7Avoided = 0.0
        val d7Threshold = today.minusDays(6)

        for ((date, summary) in dailySummaries) {
            val actual = summary.consumptionAmount + summary.slipAmount
            val avoided = max(0.0, baselineDailyAmount - actual)

            lifetimeAvoided += avoided
            if (date == today) {
                todayAvoided += avoided
            }
            if (!date.isBefore(d7Threshold) && !date.isAfter(today)) {
                last7Avoided += avoided
            }
        }

        return AvoidedResult(
            todayAvoided = roundToOneDecimal(todayAvoided),
            last7DaysAvoided = roundToOneDecimal(last7Avoided),
            lifetimeAvoided = roundToOneDecimal(lifetimeAvoided)
        )
    }

    /**
     * Time calculations for time-based habits
     */
    fun calculateTime(
        dailySummaries: Map<LocalDate, DailySummary>,
        baselineDailyMinutes: Double,
        today: LocalDate,
        goalType: String = "QUIT"
    ): TimeResult {
        val isTrack = goalType.equals("TRACK", ignoreCase = true)
        var lifetimeMinutesSpent = 0L
        var lifetimeMinutesSaved = 0L
        var todayMinutesSpent = 0L
        var todayMinutesSaved = 0L

        for ((date, summary) in dailySummaries) {
            val daySpent = (summary.consumptionAmount + summary.slipAmount).toLong()
            val daySaved = if (isTrack) 0L else max(0L, (baselineDailyMinutes - daySpent).toLong())

            lifetimeMinutesSpent += daySpent
            lifetimeMinutesSaved += daySaved

            if (date == today) {
                todayMinutesSpent = daySpent
                todayMinutesSaved = daySaved
            }
        }

        return TimeResult(
            todayMinutesSpent = todayMinutesSpent,
            todayMinutesSaved = todayMinutesSaved,
            lifetimeMinutesSpent = lifetimeMinutesSpent,
            lifetimeMinutesSaved = lifetimeMinutesSaved
        )
    }

    /**
     * Trigger analysis with Smart Insight Rule
     */
    fun calculateTriggerInsights(
        triggersWithNames: List<String>
    ): List<TriggerInsight> {
        if (triggersWithNames.isEmpty()) return emptyList()

        val counts = triggersWithNames.groupingBy { it }.eachCount()
        val total = triggersWithNames.size.toDouble()

        return counts.map { (name, count) ->
            TriggerInsight(
                name = name,
                count = count,
                percentage = ((count / total) * 1000.0).roundToInt() / 10.0
            )
        }.sortedByDescending { it.count }
    }

    /**
     * Cravings insights
     */
    fun calculateCravingInsights(
        cravings: List<CravingRawData>
    ): CravingInsight {
        if (cravings.isEmpty()) {
            return CravingInsight(0, 0, 0.0, 0.0)
        }

        val total = cravings.size
        val resisted = cravings.count { it.resisted }
        val resistedPct = ((resisted.toDouble() / total.toDouble()) * 1000.0).roundToInt() / 10.0
        val avgIntensity = ((cravings.map { it.intensity }.average() * 10.0).roundToInt()) / 10.0

        return CravingInsight(
            totalCravings = total,
            resistedCount = resisted,
            resistedPercentage = resistedPct,
            averageIntensity = avgIntensity
        )
    }

    /**
     * Progress insight with Smart Insight Rule: Requires at least 3 days of tracking data
     */
    fun calculateProgressInsight(
        dailySummaries: Map<LocalDate, DailySummary>,
        baselineDailyAmount: Double
    ): ProgressInsight {
        val totalDays = dailySummaries.size
        if (totalDays < 3) {
            return ProgressInsight(
                reductionPercentage = 0.0,
                baselineDailyAmount = baselineDailyAmount,
                currentDailyAverage = 0.0,
                hasSufficientData = false,
                message = "Keep logging to discover your patterns and reduction trends."
            )
        }

        val totalActual = dailySummaries.values.sumOf { it.consumptionAmount + it.slipAmount }
        val avgDailyActual = totalActual / totalDays.toDouble()

        val reduction = if (baselineDailyAmount > 0.0) {
            max(0.0, ((baselineDailyAmount - avgDailyActual) / baselineDailyAmount) * 100.0)
        } else 0.0

        val roundedReduction = ((reduction * 10.0).roundToInt()) / 10.0
        val roundedAvg = ((avgDailyActual * 10.0).roundToInt()) / 10.0

        val message = if (roundedReduction > 0) {
            "You have reduced your average daily consumption by $roundedReduction%."
        } else {
            "Tracking your baseline. Stay with the tide."
        }

        return ProgressInsight(
            reductionPercentage = roundedReduction,
            baselineDailyAmount = baselineDailyAmount,
            currentDailyAverage = roundedAvg,
            hasSufficientData = true,
            message = message
        )
    }

    private fun roundToTwoDecimals(value: Double): Double {
        return (value * 100.0).roundToInt() / 100.0
    }

    private fun roundToOneDecimal(value: Double): Double {
        return (value * 10.0).roundToInt() / 10.0
    }
}

// Light DTOs for calculation decoupling
data class ConsumptionRawData(
    val timestamp: Long,
    val amount: Double,
    val calculatedCost: Double,
    val triggerName: String? = null
)

data class SlipRawData(
    val timestamp: Long,
    val amount: Double,
    val cost: Double,
    val triggerName: String? = null
)

data class CravingRawData(
    val timestamp: Long,
    val intensity: Int,
    val resisted: Boolean,
    val triggerName: String? = null
)
