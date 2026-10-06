package com.tide.app.core.calculation

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate

class TideCalculationEngineTest {

    private val today = LocalDate.of(2026, 10, 6)

    // --- 1. MONEY CALCULATION TESTS ---

    @Test
    fun testMoneySaved_CleanDays_QuitMode() {
        val startDate = today.minusDays(5) // 6 days total (Day -5, -4, -3, -2, -1, 0)
        val baselineCost = 100.0 // ₹100 per day

        val summaries = TideCalculationEngine.buildDailySummaries(
            startDate = startDate,
            endDate = today,
            goalType = "QUIT",
            targetAmount = 0.0,
            consumptionEntries = emptyList(),
            slipEntries = emptyList(),
            cravingEntries = emptyList()
        )

        val moneyResult = TideCalculationEngine.calculateMoney(summaries, baselineCost, today)

        // 6 days * 100 = 600 saved
        assertEquals(600.0, moneyResult.lifetimeSaved, 0.01)
        assertEquals(0.0, moneyResult.lifetimeSpent, 0.01)
        assertEquals(100.0, moneyResult.todaySaved, 0.01)
        assertEquals(0.0, moneyResult.todaySpent, 0.01)
        // Yearly projection = 100 * 365 = 36500
        assertEquals(36500.0, moneyResult.yearlyProjectedSaved, 0.01)
    }

    @Test
    fun testMoneySaved_ReducedConsumption() {
        val startDate = today.minusDays(2) // 3 days total
        val baselineCost = 200.0

        val startMillis = TideCalculationEngine.localDateToStartOfDayMillis(today)
        val yesterdayMillis = TideCalculationEngine.localDateToStartOfDayMillis(today.minusDays(1))

        // Spent 50 yesterday, spent 100 today
        val consumptions = listOf(
            ConsumptionRawData(timestamp = yesterdayMillis + 3600000, amount = 2.0, calculatedCost = 50.0),
            ConsumptionRawData(timestamp = startMillis + 3600000, amount = 4.0, calculatedCost = 100.0)
        )

        val summaries = TideCalculationEngine.buildDailySummaries(
            startDate = startDate,
            endDate = today,
            goalType = "REDUCE",
            targetAmount = 4.0,
            consumptionEntries = consumptions,
            slipEntries = emptyList(),
            cravingEntries = emptyList()
        )

        val money = TideCalculationEngine.calculateMoney(summaries, baselineCost, today)

        // Day -2: 0 spent -> 200 saved
        // Day -1: 50 spent -> 150 saved
        // Day 0: 100 spent -> 100 saved
        // Total saved = 450, Total spent = 150
        assertEquals(150.0, money.lifetimeSpent, 0.01)
        assertEquals(450.0, money.lifetimeSaved, 0.01)
        assertEquals(100.0, money.todaySaved, 0.01)
        assertEquals(100.0, money.todaySpent, 0.01)
    }

    // --- 2. CONSUMPTION & AVOIDED AMOUNT TESTS ---

    @Test
    fun testAvoidedConsumption_QuitMode() {
        val startDate = today.minusDays(11) // 12 days total
        val baselineAmount = 8.0 // 8 cigarettes per day

        val summaries = TideCalculationEngine.buildDailySummaries(
            startDate = startDate,
            endDate = today,
            goalType = "QUIT",
            targetAmount = 0.0,
            consumptionEntries = emptyList(),
            slipEntries = emptyList(),
            cravingEntries = emptyList()
        )

        val avoided = TideCalculationEngine.calculateAvoided(summaries, baselineAmount, today)

        // 12 days * 8 = 96 units avoided (prompt requirement: 96 units)
        assertEquals(96.0, avoided.lifetimeAvoided, 0.1)
        assertEquals(8.0, avoided.todayAvoided, 0.1)
    }

    @Test
    fun testAvoidedConsumption_ReduceMode() {
        val startDate = today.minusDays(1) // 2 days
        val baselineAmount = 8.0

        val startMillis = TideCalculationEngine.localDateToStartOfDayMillis(today)
        val consumptions = listOf(
            ConsumptionRawData(timestamp = startMillis + 1000, amount = 4.0, calculatedCost = 40.0)
        )

        val summaries = TideCalculationEngine.buildDailySummaries(
            startDate = startDate,
            endDate = today,
            goalType = "REDUCE",
            targetAmount = 4.0,
            consumptionEntries = consumptions,
            slipEntries = emptyList(),
            cravingEntries = emptyList()
        )

        val avoided = TideCalculationEngine.calculateAvoided(summaries, baselineAmount, today)

        // Yesterday: 8 avoided
        // Today: 8 - 4 = 4 avoided
        // Total = 12
        assertEquals(4.0, avoided.todayAvoided, 0.1)
        assertEquals(12.0, avoided.lifetimeAvoided, 0.1)
    }

    // --- 3. STREAK ENGINE TESTS ---

    @Test
    fun testStreaks_ConsecutiveCleanDays() {
        val startDate = today.minusDays(6) // 7 days

        val summaries = TideCalculationEngine.buildDailySummaries(
            startDate = startDate,
            endDate = today,
            goalType = "QUIT",
            targetAmount = 0.0,
            consumptionEntries = emptyList(),
            slipEntries = emptyList(),
            cravingEntries = emptyList()
        )

        val streak = TideCalculationEngine.calculateStreaks(summaries, today)

        assertEquals(7, streak.currentStreak)
        assertEquals(7, streak.longestStreak)
        assertEquals(7, streak.totalCleanDays)
        assertEquals(100.0, streak.successRate, 0.1)
    }

    @Test
    fun testStreaks_SlipResetsCurrentStreak_PreservesLongestStreakAndCleanDays() {
        // 10 days: clean 6 days, slip on day 7, clean 3 days
        val startDate = today.minusDays(9)

        // Slip happened 3 days ago (today - 3)
        val slipDate = today.minusDays(3)
        val slipMillis = TideCalculationEngine.localDateToStartOfDayMillis(slipDate)

        val slips = listOf(
            SlipRawData(timestamp = slipMillis + 5000, amount = 1.0, cost = 20.0)
        )

        val summaries = TideCalculationEngine.buildDailySummaries(
            startDate = startDate,
            endDate = today,
            goalType = "QUIT",
            targetAmount = 0.0,
            consumptionEntries = emptyList(),
            slipEntries = slips,
            cravingEntries = emptyList()
        )

        val streak = TideCalculationEngine.calculateStreaks(summaries, today)

        // Current streak: today, today-1, today-2 = 3 days clean
        assertEquals(3, streak.currentStreak)
        // Longest streak was the initial 6 days
        assertEquals(6, streak.longestStreak)
        // Total clean days = 9 out of 10
        assertEquals(9, streak.totalCleanDays)
        assertEquals(90.0, streak.successRate, 0.1)
    }

    @Test
    fun testStreaks_MultipleSlips() {
        val startDate = today.minusDays(4) // 5 days

        // Slips on today - 3 and today
        val slips = listOf(
            SlipRawData(timestamp = TideCalculationEngine.localDateToStartOfDayMillis(today.minusDays(3)) + 1000, amount = 1.0, cost = 10.0),
            SlipRawData(timestamp = TideCalculationEngine.localDateToStartOfDayMillis(today) + 1000, amount = 1.0, cost = 10.0)
        )

        val summaries = TideCalculationEngine.buildDailySummaries(
            startDate = startDate,
            endDate = today,
            goalType = "QUIT",
            targetAmount = 0.0,
            consumptionEntries = emptyList(),
            slipEntries = slips,
            cravingEntries = emptyList()
        )

        val streak = TideCalculationEngine.calculateStreaks(summaries, today)

        assertEquals(0, streak.currentStreak) // Broke today
        assertEquals(2, streak.longestStreak) // Days today-2 and today-1 were clean
        assertEquals(3, streak.totalCleanDays) // 3 clean days total
    }

    // --- 4. GOAL MODES TESTS ---

    @Test
    fun testReduceMode_TargetMetIsClean() {
        val startMillis = TideCalculationEngine.localDateToStartOfDayMillis(today)

        val consumptions = listOf(
            ConsumptionRawData(timestamp = startMillis + 1000, amount = 3.0, calculatedCost = 30.0)
        )

        val summaries = TideCalculationEngine.buildDailySummaries(
            startDate = today,
            endDate = today,
            goalType = "REDUCE",
            targetAmount = 4.0, // target is 4, actual is 3 -> clean!
            consumptionEntries = consumptions,
            slipEntries = emptyList(),
            cravingEntries = emptyList()
        )

        val summary = summaries[today]!!
        assertTrue(summary.isClean)
    }

    @Test
    fun testReduceMode_TargetExceededIsNotClean() {
        val startMillis = TideCalculationEngine.localDateToStartOfDayMillis(today)

        val consumptions = listOf(
            ConsumptionRawData(timestamp = startMillis + 1000, amount = 5.0, calculatedCost = 50.0)
        )

        val summaries = TideCalculationEngine.buildDailySummaries(
            startDate = today,
            endDate = today,
            goalType = "REDUCE",
            targetAmount = 4.0, // target is 4, actual is 5 -> not clean
            consumptionEntries = consumptions,
            slipEntries = emptyList(),
            cravingEntries = emptyList()
        )

        val summary = summaries[today]!!
        assertFalse(summary.isClean)
    }

    // --- 5. INSIGHTS TESTS (SMART INSIGHT RULE) ---

    @Test
    fun testInsights_InsufficientDataRule() {
        val startDate = today.minusDays(1) // only 2 days

        val summaries = TideCalculationEngine.buildDailySummaries(
            startDate = startDate,
            endDate = today,
            goalType = "QUIT",
            targetAmount = 0.0,
            consumptionEntries = emptyList(),
            slipEntries = emptyList(),
            cravingEntries = emptyList()
        )

        val progress = TideCalculationEngine.calculateProgressInsight(summaries, 8.0)

        // Smart insight rule: insufficient data
        assertFalse(progress.hasSufficientData)
        assertTrue(progress.message.contains("Keep logging"))
    }

    @Test
    fun testInsights_SufficientDataRule() {
        val startDate = today.minusDays(3) // 4 days

        val summaries = TideCalculationEngine.buildDailySummaries(
            startDate = startDate,
            endDate = today,
            goalType = "QUIT",
            targetAmount = 0.0,
            consumptionEntries = emptyList(),
            slipEntries = emptyList(),
            cravingEntries = emptyList()
        )

        val progress = TideCalculationEngine.calculateProgressInsight(summaries, 8.0)

        assertTrue(progress.hasSufficientData)
        assertEquals(100.0, progress.reductionPercentage, 0.1)
    }

    @Test
    fun testTriggerPercentages() {
        val triggers = listOf(
            "Stress", "Stress", "Boredom", "Social situation"
        )

        val insight = TideCalculationEngine.calculateTriggerInsights(triggers)

        assertEquals(3, insight.size)
        val stress = insight.find { it.name == "Stress" }!!
        assertEquals(2, stress.count)
        assertEquals(50.0, stress.percentage, 0.1) // 2/4 = 50%
    }

    @Test
    fun testCravingAnalysis() {
        val cravings = listOf(
            CravingRawData(timestamp = 1000, intensity = 8, resisted = true),
            CravingRawData(timestamp = 2000, intensity = 6, resisted = true),
            CravingRawData(timestamp = 3000, intensity = 4, resisted = false)
        )

        val result = TideCalculationEngine.calculateCravingInsights(cravings)

        assertEquals(3, result.totalCravings)
        assertEquals(2, result.resistedCount)
        assertEquals(66.7, result.resistedPercentage, 0.1)
        assertEquals(6.0, result.averageIntensity, 0.1) // (8+6+4)/3 = 6.0
    }
}
