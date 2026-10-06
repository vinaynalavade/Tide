package com.tide.app.core.calculation

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate

class TideEdgeCasesTest {

    @Test
    fun testMonthBoundaryTransition() {
        val startOfCycle = LocalDate.of(2026, 9, 28) // September 28
        val endOfCycle = LocalDate.of(2026, 10, 3)   // October 3 (6 days)
        val baselineDailyCost = 150.0

        val summaries = TideCalculationEngine.buildDailySummaries(
            startDate = startOfCycle,
            endDate = endOfCycle,
            goalType = "QUIT",
            targetAmount = 0.0,
            consumptionEntries = emptyList(),
            slipEntries = emptyList(),
            cravingEntries = emptyList()
        )

        assertEquals(6, summaries.size)
        assertTrue(summaries.containsKey(LocalDate.of(2026, 9, 30)))
        assertTrue(summaries.containsKey(LocalDate.of(2026, 10, 1)))

        val money = TideCalculationEngine.calculateMoney(summaries, baselineDailyCost, endOfCycle)
        // 6 days * 150 = 900
        assertEquals(900.0, money.lifetimeSaved, 0.01)
    }

    @Test
    fun testMultipleSlipsOnSameDay() {
        val today = LocalDate.of(2026, 10, 6)
        val todayStart = TideCalculationEngine.localDateToStartOfDayMillis(today)

        val slips = listOf(
            SlipRawData(timestamp = todayStart + 1000, amount = 1.0, cost = 20.0),
            SlipRawData(timestamp = todayStart + 5000, amount = 2.0, cost = 40.0)
        )

        val summaries = TideCalculationEngine.buildDailySummaries(
            startDate = today,
            endDate = today,
            goalType = "QUIT",
            targetAmount = 0.0,
            consumptionEntries = emptyList(),
            slipEntries = slips,
            cravingEntries = emptyList()
        )

        val daySummary = summaries[today]!!
        assertEquals(3.0, daySummary.slipAmount, 0.01)
        assertEquals(60.0, daySummary.slipCost, 0.01)
        assertFalse(daySummary.isClean)

        val streak = TideCalculationEngine.calculateStreaks(summaries, today)
        assertEquals(0, streak.currentStreak)
    }

    @Test
    fun testTrackModeAlwaysClean() {
        val today = LocalDate.of(2026, 10, 6)
        val todayStart = TideCalculationEngine.localDateToStartOfDayMillis(today)

        val consumptions = listOf(
            ConsumptionRawData(timestamp = todayStart + 2000, amount = 10.0, calculatedCost = 200.0)
        )

        val summaries = TideCalculationEngine.buildDailySummaries(
            startDate = today,
            endDate = today,
            goalType = "TRACK",
            targetAmount = 0.0,
            consumptionEntries = consumptions,
            slipEntries = emptyList(),
            cravingEntries = emptyList()
        )

        val daySummary = summaries[today]!!
        // TRACK mode has no failure state
        assertTrue(daySummary.isClean)
    }

    @Test
    fun testFirstCleanDay() {
        val today = LocalDate.of(2026, 10, 6)

        val summaries = TideCalculationEngine.buildDailySummaries(
            startDate = today,
            endDate = today,
            goalType = "QUIT",
            targetAmount = 0.0,
            consumptionEntries = emptyList(),
            slipEntries = emptyList(),
            cravingEntries = emptyList()
        )

        val streak = TideCalculationEngine.calculateStreaks(summaries, today)
        assertEquals(1, streak.currentStreak)
        assertEquals(1, streak.longestStreak)
        assertEquals(1, streak.totalCleanDays)
        assertEquals(100.0, streak.successRate, 0.01)
    }

    @Test
    fun testEmptyDataGracefulHandling() {
        val emptySummaries = emptyMap<LocalDate, DailySummary>()
        val today = LocalDate.of(2026, 10, 6)

        val streaks = TideCalculationEngine.calculateStreaks(emptySummaries, today)
        assertEquals(0, streaks.currentStreak)
        assertEquals(0, streaks.longestStreak)

        val money = TideCalculationEngine.calculateMoney(emptySummaries, 100.0, today)
        assertEquals(0.0, money.lifetimeSaved, 0.01)
        assertEquals(0.0, money.lifetimeSpent, 0.01)

        val avoided = TideCalculationEngine.calculateAvoided(emptySummaries, 10.0, today)
        assertEquals(0.0, avoided.lifetimeAvoided, 0.01)

        val triggerInsights = TideCalculationEngine.calculateTriggerInsights(emptyList())
        assertTrue(triggerInsights.isEmpty())

        val cravingInsights = TideCalculationEngine.calculateCravingInsights(emptyList())
        assertEquals(0, cravingInsights.totalCravings)
        assertEquals(0.0, cravingInsights.averageIntensity, 0.01)
    }
}
