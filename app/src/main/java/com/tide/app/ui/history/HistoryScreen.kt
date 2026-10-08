package com.tide.app.ui.history

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Air
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Mood
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tide.app.core.designsystem.MetricCard
import com.tide.app.core.designsystem.TideCard
import com.tide.app.core.designsystem.TideTopBar
import com.tide.app.core.theme.TidePrimary
import com.tide.app.core.theme.TidePrimaryLuminous
import com.tide.app.core.theme.TideSlip
import com.tide.app.core.theme.TideSuccess
import com.tide.app.core.theme.TideTheme
import java.time.Instant
import java.time.LocalDate
import java.time.YearMonth
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@Composable
fun HistoryScreen(
    viewModel: HistoryViewModel
) {
    val state by viewModel.uiState.collectAsState()

    if (state.isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = TidePrimary)
        }
        return
    }

    val selectedMonth = state.selectedMonth
    val monthTitle = selectedMonth.format(DateTimeFormatter.ofPattern("MMMM yyyy"))

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(bottom = 100.dp)
    ) {
        TideTopBar(
            title = "History",
            subtitle = "Your tracking calendar and daily records"
        )

        // Multi-habit selector chips
        if (state.activeHabits.size > 1) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                state.activeHabits.forEach { habit ->
                    val isSelected = habit.id == state.selectedHabit?.id
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (isSelected) TidePrimary.copy(alpha = 0.2f) else TideTheme.colors.cardBorder.copy(alpha = 0.2f),
                        border = BorderStroke(1.dp, if (isSelected) TidePrimaryLuminous else Color.Transparent),
                        modifier = Modifier.clickable { viewModel.selectHabit(habit.id) }
                    ) {
                        Text(
                            text = habit.name,
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) TidePrimaryLuminous else TideTheme.colors.textPrimary,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
        }

        if (state.selectedHabit == null) {
            Box(modifier = Modifier.fillMaxWidth().padding(40.dp), contentAlignment = Alignment.Center) {
                Text("No active habits found.", color = TideTheme.colors.textSecondary)
            }
            return
        }

        // Month Selector Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = monthTitle,
                style = MaterialTheme.typography.titleLarge,
                color = TideTheme.colors.textPrimary
            )

            Row {
                IconButton(onClick = { viewModel.changeMonth(-1) }) {
                    Icon(Icons.AutoMirrored.Filled.KeyboardArrowLeft, contentDescription = "Previous Month", tint = TideTheme.colors.textPrimary)
                }
                IconButton(onClick = { viewModel.changeMonth(1) }) {
                    Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = "Next Month", tint = TideTheme.colors.textPrimary)
                }
            }
        }

        // Calendar Grid
        CalendarGrid(
            selectedMonth = selectedMonth,
            dailySummaries = state.dailySummaries,
            onDayClick = { viewModel.selectDate(it) }
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Accessible Legend
        CalendarLegend()

        Spacer(modifier = Modifier.height(24.dp))
    }

    // Day Details Bottom Sheet
    state.selectedDayDetail?.let { detail ->
        DayDetailBottomSheet(
            detail = detail,
            onDismiss = { viewModel.clearSelectedDay() }
        )
    }
}

@Composable
private fun CalendarGrid(
    selectedMonth: YearMonth,
    dailySummaries: Map<LocalDate, com.tide.app.core.calculation.DailySummary>,
    onDayClick: (LocalDate) -> Unit
) {
    val daysOfWeek = listOf("S", "M", "T", "W", "T", "F", "S")
    val firstDayOfMonth = selectedMonth.atDay(1)
    val dayOfWeekOffset = firstDayOfMonth.dayOfWeek.value % 7 // 0 for Sunday
    val daysInMonth = selectedMonth.lengthOfMonth()
    val today = LocalDate.now()

    TideCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
    ) {
        Column {
            // Days of Week Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                daysOfWeek.forEach { dayName ->
                    Text(
                        text = dayName,
                        style = MaterialTheme.typography.labelMedium,
                        color = TideTheme.colors.textTertiary,
                        modifier = Modifier.width(36.dp),
                        textAlign = TextAlign.Center
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Calendar Weeks
            val totalSlots = dayOfWeekOffset + daysInMonth
            val totalRows = (totalSlots + 6) / 7

            for (row in 0 until totalRows) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    for (col in 0 until 7) {
                        val slotIndex = row * 7 + col
                        val dayNumber = slotIndex - dayOfWeekOffset + 1

                        if (dayNumber in 1..daysInMonth) {
                            val date = selectedMonth.atDay(dayNumber)
                            val summary = dailySummaries[date]
                            val isFuture = date.isAfter(today)

                            DayCell(
                                dayNumber = dayNumber,
                                date = date,
                                isToday = date == today,
                                isFuture = isFuture,
                                summary = summary,
                                onClick = { if (!isFuture) onDayClick(date) }
                            )
                        } else {
                            Spacer(modifier = Modifier.width(36.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DayCell(
    dayNumber: Int,
    date: LocalDate,
    isToday: Boolean,
    isFuture: Boolean,
    summary: com.tide.app.core.calculation.DailySummary?,
    onClick: () -> Unit
) {
    val statusColor = when {
        isFuture -> Color.Transparent
        summary == null -> Color.Transparent
        summary.slipAmount > 0 -> TideSlip
        summary.isClean -> TideSuccess
        else -> TideTheme.colors.warning
    }

    Surface(
        shape = RoundedCornerShape(10.dp),
        color = if (isToday) TidePrimary.copy(alpha = 0.15f) else Color.Transparent,
        border = if (isToday) BorderStroke(1.dp, TidePrimaryLuminous) else null,
        modifier = Modifier
            .size(36.dp)
            .clickable(enabled = !isFuture, onClick = onClick)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            Text(
                text = "$dayNumber",
                fontSize = 12.sp,
                fontWeight = if (isToday) FontWeight.Bold else FontWeight.Normal,
                color = if (isFuture) TideTheme.colors.textTertiary.copy(alpha = 0.4f) else TideTheme.colors.textPrimary
            )
            if (!isFuture && summary != null) {
                Spacer(modifier = Modifier.height(2.dp))
                Surface(
                    shape = CircleShape,
                    color = statusColor,
                    modifier = Modifier.size(5.dp)
                ) {}
            }
        }
    }
}

@Composable
private fun CalendarLegend() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        horizontalArrangement = Arrangement.SpaceAround
    ) {
        LegendItem(color = TideSuccess, label = "Clean / Met")
        LegendItem(color = TideTheme.colors.warning, label = "Reduced")
        LegendItem(color = TideSlip, label = "Slip")
        LegendItem(color = TideTheme.colors.textTertiary, label = "No log")
    }
}

@Composable
private fun LegendItem(color: Color, label: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Surface(shape = CircleShape, color = color, modifier = Modifier.size(8.dp)) {}
        Spacer(modifier = Modifier.width(6.dp))
        Text(text = label, style = MaterialTheme.typography.bodySmall, color = TideTheme.colors.textSecondary)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DayDetailBottomSheet(
    detail: HistoryDayDetail,
    onDismiss: () -> Unit
) {
    val dateHeader = detail.date.format(DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy"))

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = TideTheme.colors.cardBackground,
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 16.dp)
        ) {
            Text(
                text = dateHeader,
                style = MaterialTheme.typography.headlineMedium,
                color = TideTheme.colors.textPrimary
            )

            Spacer(modifier = Modifier.height(6.dp))

            val statusText = when {
                detail.hasSlip -> "Slip recorded • Information, not a verdict"
                detail.isClean -> "Clean day • Goal achieved"
                else -> "Active reduction day"
            }
            Text(
                text = statusText,
                style = MaterialTheme.typography.bodyMedium,
                color = if (detail.hasSlip) TideSlip else TideSuccess
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Metrics Cards
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                MetricCard(
                    title = "Consumption",
                    value = "${detail.consumptionAmount.toInt()} ${detail.unit}",
                    subtitle = "Target: ${detail.targetAmount.toInt()}",
                    modifier = Modifier.weight(1f)
                )
                MetricCard(
                    title = "Cravings",
                    value = "${detail.cravingsCount}",
                    subtitle = "${detail.cravingsResistedCount} resisted",
                    modifier = Modifier.weight(1f)
                )
            }

            if (detail.hasFinancialImpact) {
                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    MetricCard(
                        title = "Money Spent",
                        value = "₹${detail.moneySpent.toInt()}",
                        accentColor = TideSlip,
                        modifier = Modifier.weight(1f)
                    )
                    MetricCard(
                        title = "Money Saved",
                        value = "₹${detail.moneySaved.toInt()}",
                        accentColor = TideSuccess,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            if (detail.mood != null) {
                Spacer(modifier = Modifier.height(16.dp))
                TideCard {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Mood, contentDescription = null, tint = TidePrimaryLuminous, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Daily Check-In Mood: ${detail.mood}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TideTheme.colors.textPrimary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Event Logs for this day
            Text(
                text = "Entries on this Day",
                style = MaterialTheme.typography.titleMedium,
                color = TideTheme.colors.textPrimary
            )
            Spacer(modifier = Modifier.height(8.dp))

            if (detail.consumptions.isEmpty() && detail.cravings.isEmpty() && detail.slips.isEmpty()) {
                Text(
                    text = "No events logged on this date.",
                    style = MaterialTheme.typography.bodySmall,
                    color = TideTheme.colors.textSecondary
                )
            } else {
                detail.consumptions.forEach { c ->
                    val costLabel = if (detail.hasFinancialImpact) "₹${c.calculatedCost.toInt()}" else "Logged"
                    EntryRow(title = "Consumed ${c.amount.toInt()} ${detail.unit}", subtitle = costLabel, timestamp = c.timestamp, icon = Icons.Default.Add, color = TidePrimaryLuminous)
                    Spacer(modifier = Modifier.height(6.dp))
                }
                detail.slips.forEach { s ->
                    EntryRow(title = "Slip: ${s.amount.toInt()} ${detail.unit}", subtitle = s.mood ?: "Recorded", timestamp = s.timestamp, icon = Icons.Outlined.Warning, color = TideSlip)
                    Spacer(modifier = Modifier.height(6.dp))
                }
                detail.cravings.forEach { cr ->
                    EntryRow(title = "Craving (Intensity ${cr.intensity}/10)", subtitle = if (cr.resisted) "Resisted" else "Acted upon", timestamp = cr.timestamp, icon = Icons.Default.Air, color = TideTheme.colors.warning)
                    Spacer(modifier = Modifier.height(6.dp))
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
private fun EntryRow(
    title: String,
    subtitle: String,
    timestamp: Long,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color
) {
    val time = Instant.ofEpochMilli(timestamp)
        .atZone(ZoneId.systemDefault())
        .format(DateTimeFormatter.ofPattern("h:mm a"))

    TideCard {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(shape = CircleShape, color = color.copy(alpha = 0.15f), modifier = Modifier.size(32.dp)) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(16.dp))
                    }
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(title, style = MaterialTheme.typography.bodyMedium, color = TideTheme.colors.textPrimary)
                    Text(subtitle, style = MaterialTheme.typography.bodySmall, color = TideTheme.colors.textSecondary)
                }
            }
            Text(time, style = MaterialTheme.typography.labelSmall, color = TideTheme.colors.textTertiary)
        }
    }
}
