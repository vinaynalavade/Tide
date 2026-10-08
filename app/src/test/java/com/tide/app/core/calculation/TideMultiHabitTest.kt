package com.tide.app.core.calculation

import com.tide.app.data.local.TideDatabase
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate

class TideMultiHabitTest {

    private val today = LocalDate.of(2026, 10, 8)

    // =========================================================================
    // 1. NON-FINANCIAL HABIT (SUGAR / GAMING) GUARANTEES
    // =========================================================================

    @Test
    fun testNonFinancialHabit_ZeroMoneyGuaranteed() {
        val startDate = today.minusDays(5)
        val baselineCost = 0.0 // No monetary cost
        val baselineAmount = 4.0 // 4 servings

        val summaries = TideCalculationEngine.buildDailySummaries(
            startDate = startDate,
            endDate = today,
            goalType = "REDUCE",
            targetAmount = 2.0,
            consumptionEntries = emptyList(),
            slipEntries = emptyList(),
            cravingEntries = emptyList()
        )

        // hasFinancialImpact = false
        val money = TideCalculationEngine.calculateMoney(
            dailySummaries = summaries,
            baselineDailyCost = baselineCost,
            today = today,
            hasFinancialImpact = false,
            goalType = "REDUCE"
        )

        // Guarantees: No fake money values or misleading "₹0 saved"
        assertEquals(0.0, money.lifetimeSaved, 0.001)
        assertEquals(0.0, money.lifetimeSpent, 0.001)
        assertEquals(0.0, money.todaySaved, 0.001)
        assertEquals(0.0, money.todaySpent, 0.001)
        assertEquals(0.0, money.yearlyProjectedSaved, 0.001)
        assertEquals(0.0, money.last30DaysSaved, 0.001)
    }

    // =========================================================================
    // 2. REDUCE MODE SEMANTICS (SUGAR: BASELINE 4, TARGET 2)
    // =========================================================================

    @Test
    fun testReduceMode_WithinTarget_IsCleanAndAvoidedCalculated() {
        val todayStart = TideCalculationEngine.localDateToStartOfDayMillis(today)
        val baselineAmount = 4.0
        val targetAmount = 2.0

        // Consumed 1 serving today (within target of 2)
        val consumptions = listOf(
            ConsumptionRawData(timestamp = todayStart + 1000, amount = 1.0, calculatedCost = 0.0)
        )

        val summaries = TideCalculationEngine.buildDailySummaries(
            startDate = today,
            endDate = today,
            goalType = "REDUCE",
            targetAmount = targetAmount,
            consumptionEntries = consumptions,
            slipEntries = emptyList(),
            cravingEntries = emptyList()
        )

        val daySummary = summaries[today]!!
        // Within target of 2 -> clean
        assertTrue(daySummary.isClean)
        assertEquals(1.0, daySummary.consumptionAmount, 0.01)

        val avoided = TideCalculationEngine.calculateAvoided(
            dailySummaries = summaries,
            baselineDailyAmount = baselineAmount,
            today = today,
            goalType = "REDUCE"
        )

        // Baseline (4) - Consumed (1) = 3 avoided
        assertEquals(3.0, avoided.todayAvoided, 0.01)
        assertEquals(3.0, avoided.lifetimeAvoided, 0.01)
    }

    @Test
    fun testReduceMode_ExceededTarget_NotClean() {
        val todayStart = TideCalculationEngine.localDateToStartOfDayMillis(today)
        val baselineAmount = 4.0
        val targetAmount = 2.0

        // Consumed 3 servings today (target was 2)
        val consumptions = listOf(
            ConsumptionRawData(timestamp = todayStart + 1000, amount = 3.0, calculatedCost = 0.0)
        )

        val summaries = TideCalculationEngine.buildDailySummaries(
            startDate = today,
            endDate = today,
            goalType = "REDUCE",
            targetAmount = targetAmount,
            consumptionEntries = consumptions,
            slipEntries = emptyList(),
            cravingEntries = emptyList()
        )

        val daySummary = summaries[today]!!
        // Exceeded target -> not clean
        assertFalse(daySummary.isClean)
        assertEquals(3.0, daySummary.consumptionAmount, 0.01)
    }

    // =========================================================================
    // 3. TRACK MODE SEMANTICS (DOOMSCROLLING / GAMING)
    // =========================================================================

    @Test
    fun testTrackMode_NonJudgmentalTracking_NoAvoidedMetrics() {
        val todayStart = TideCalculationEngine.localDateToStartOfDayMillis(today)
        val baselineAmount = 120.0 // 120 minutes

        val consumptions = listOf(
            ConsumptionRawData(timestamp = todayStart + 1000, amount = 45.0, calculatedCost = 0.0),
            ConsumptionRawData(timestamp = todayStart + 5000, amount = 30.0, calculatedCost = 0.0)
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
        // TRACK mode has no failure condition
        assertTrue(daySummary.isClean)
        assertEquals(75.0, daySummary.consumptionAmount, 0.01)

        val avoided = TideCalculationEngine.calculateAvoided(
            dailySummaries = summaries,
            baselineDailyAmount = baselineAmount,
            today = today,
            goalType = "TRACK"
        )

        // In TRACK mode, avoided should be 0 (no avoided judgement)
        assertEquals(0.0, avoided.todayAvoided, 0.001)
        assertEquals(0.0, avoided.lifetimeAvoided, 0.001)
    }

    // =========================================================================
    // 4. TIME IMPACT CALCULATION
    // =========================================================================

    @Test
    fun testTimeImpactCalculation() {
        val todayStart = TideCalculationEngine.localDateToStartOfDayMillis(today)
        val baselineMinutes = 120.0

        val consumptions = listOf(
            ConsumptionRawData(timestamp = todayStart + 2000, amount = 45.0, calculatedCost = 0.0)
        )

        val summaries = TideCalculationEngine.buildDailySummaries(
            startDate = today,
            endDate = today,
            goalType = "REDUCE",
            targetAmount = 60.0,
            consumptionEntries = consumptions,
            slipEntries = emptyList(),
            cravingEntries = emptyList()
        )

        val timeResult = TideCalculationEngine.calculateTime(
            dailySummaries = summaries,
            baselineDailyMinutes = baselineMinutes,
            today = today,
            goalType = "REDUCE"
        )

        assertEquals(45L, timeResult.todayMinutesSpent)
        // Baseline 120 - 45 = 75 saved/reclaimed minutes
        assertEquals(75L, timeResult.todayMinutesSaved)
        assertEquals(75L, timeResult.lifetimeMinutesSaved)
    }

    // =========================================================================
    // 5. HISTORICAL EVENT SNAPSHOT COST IMMUTABILITY
    // =========================================================================

    @Test
    fun testHistoricalSnapshotCost_ImmutableWhenPriceChanges() {
        val day1 = today.minusDays(1)
        val day1Start = TideCalculationEngine.localDateToStartOfDayMillis(day1)
        val day2Start = TideCalculationEngine.localDateToStartOfDayMillis(today)

        // On Day 1: Price was ₹20 per unit. Consumed 5 units -> snapshot cost = ₹100
        val day1Entries = listOf(
            ConsumptionRawData(timestamp = day1Start + 1000, amount = 5.0, calculatedCost = 100.0)
        )
        // On Day 2: User changed price to ₹25 per unit. Consumed 4 units -> snapshot cost = ₹100
        val day2Entries = listOf(
            ConsumptionRawData(timestamp = day2Start + 1000, amount = 4.0, calculatedCost = 100.0)
        )

        val allEntries = day1Entries + day2Entries

        val summaries = TideCalculationEngine.buildDailySummaries(
            startDate = day1,
            endDate = today,
            goalType = "QUIT",
            targetAmount = 0.0,
            consumptionEntries = allEntries,
            slipEntries = emptyList(),
            cravingEntries = emptyList()
        )

        val day1Summary = summaries[day1]!!
        val day2Summary = summaries[today]!!

        // Day 1 cost must remain ₹100 (never recalculated to 5 * 25 = 125)
        assertEquals(100.0, day1Summary.consumptionCost, 0.01)
        assertEquals(100.0, day2Summary.consumptionCost, 0.01)

        val money = TideCalculationEngine.calculateMoney(
            dailySummaries = summaries,
            baselineDailyCost = 200.0,
            today = today,
            hasFinancialImpact = true,
            goalType = "QUIT"
        )

        assertEquals(200.0, money.lifetimeSpent, 0.01)
        // Day 1: 200 - 100 = 100 saved. Day 2: 200 - 100 = 100 saved. Total = 200 saved
        assertEquals(200.0, money.lifetimeSaved, 0.01)
    }

    // =========================================================================
    // 6. DIVISION BY ZERO & EDGE CASE PROTECTIONS
    // =========================================================================

    @Test
    fun testZeroValuesAndDivisionProtections() {
        val summaries = TideCalculationEngine.buildDailySummaries(
            startDate = today,
            endDate = today,
            goalType = "TRACK",
            targetAmount = 0.0,
            consumptionEntries = emptyList(),
            slipEntries = emptyList(),
            cravingEntries = emptyList()
        )

        // Baseline = 0.0
        val money = TideCalculationEngine.calculateMoney(
            dailySummaries = summaries,
            baselineDailyCost = 0.0,
            today = today,
            hasFinancialImpact = true,
            goalType = "TRACK"
        )

        assertFalse(money.lifetimeSaved.isNaN())
        assertFalse(money.lifetimeSaved.isInfinite())
        assertEquals(0.0, money.lifetimeSaved, 0.001)

        val avoided = TideCalculationEngine.calculateAvoided(
            dailySummaries = summaries,
            baselineDailyAmount = 0.0,
            today = today,
            goalType = "TRACK"
        )

        assertFalse(avoided.lifetimeAvoided.isNaN())
        assertFalse(avoided.lifetimeAvoided.isInfinite())
        assertEquals(0.0, avoided.lifetimeAvoided, 0.001)

        val time = TideCalculationEngine.calculateTime(
            dailySummaries = summaries,
            baselineDailyMinutes = 0.0,
            today = today,
            goalType = "TRACK"
        )

        assertEquals(0L, time.lifetimeMinutesSaved)
        assertEquals(0L, time.lifetimeMinutesSpent)
    }

    // =========================================================================
    // 7. ROOM MIGRATION 1 -> 2 INTEGRITY CHECK
    // =========================================================================

    @Test
    fun testMigration1To2Definition() {
        val migration = TideDatabase.MIGRATION_1_2
        assertEquals(1, migration.startVersion)
        assertEquals(2, migration.endVersion)
    }
}
