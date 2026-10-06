package com.tide.app.ui.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Air
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.Equalizer
import androidx.compose.material.icons.filled.LocalAtm
import androidx.compose.material.icons.filled.Mood
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Warning
import kotlin.math.max
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tide.app.core.designsystem.MetricCard
import com.tide.app.core.designsystem.TideCard
import com.tide.app.core.designsystem.TideCravingBannerButton
import com.tide.app.core.designsystem.TideGlassCard
import com.tide.app.core.theme.TidePrimary
import com.tide.app.core.theme.TidePrimaryLuminous
import com.tide.app.core.theme.TideSlip
import com.tide.app.core.theme.TideSuccess
import com.tide.app.core.theme.TideTheme
import com.tide.app.ui.tracking.DailyCheckInBottomSheet
import com.tide.app.ui.tracking.LogConsumptionBottomSheet
import com.tide.app.ui.tracking.LogCravingBottomSheet
import com.tide.app.ui.tracking.LogSlipBottomSheet
import com.tide.app.ui.tracking.QuickLogBottomSheet
import java.time.Instant
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onNavigateToCravingMode: () -> Unit,
    showQuickLogSheet: Boolean = false,
    onDismissQuickLogSheet: () -> Unit = {}
) {
    val state by viewModel.uiState.collectAsState()

    var showConsumptionSheet by remember { mutableStateOf(false) }
    var showCravingSheet by remember { mutableStateOf(false) }
    var showSlipSheet by remember { mutableStateOf(false) }
    var showCheckInSheet by remember { mutableStateOf(false) }

    if (state.isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = TidePrimary)
        }
        return
    }

    val addiction = state.addiction ?: return

    val currentHour = LocalTime.now().hour
    val greeting = when {
        currentHour < 12 -> "Good morning"
        currentHour < 17 -> "Good afternoon"
        else -> "Good evening"
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp)
            .padding(top = 16.dp, bottom = 100.dp)
    ) {
        // Editorial Greeting
        Text(
            text = greeting,
            style = MaterialTheme.typography.labelLarge,
            color = TideTheme.colors.textSecondary
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = "Stay with the tide.",
            style = MaterialTheme.typography.headlineLarge,
            color = TideTheme.colors.textPrimary
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Adaptive Hero Goal Card
        AdaptiveHeroCard(
            goalType = addiction.goalType,
            addictionName = addiction.name,
            unit = addiction.unit,
            streakDays = state.streak.currentStreak,
            todayCount = state.todayConsumptionCount,
            targetAmount = state.goal?.targetAmount ?: 0.0,
            elapsedDays = state.streak.totalElapsedDays
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Craving Mode Banner Button
        TideCravingBannerButton(onClick = onNavigateToCravingMode)

        Spacer(modifier = Modifier.height(20.dp))

        // 2x2 Key Metric Cards - Goal-Adaptive
        when (addiction.goalType.uppercase()) {
            "QUIT" -> {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    MetricCard(
                        title = "Money Saved",
                        value = "₹${state.money.lifetimeSaved.toInt()}",
                        subtitle = "₹${state.money.todaySaved.toInt()} today",
                        icon = Icons.Default.LocalAtm,
                        accentColor = TideSuccess,
                        modifier = Modifier.weight(1f)
                    )
                    MetricCard(
                        title = "Avoided",
                        value = "${state.avoided.lifetimeAvoided.toInt()}",
                        subtitle = "${state.avoided.todayAvoided.toInt()} today (${addiction.unit})",
                        icon = Icons.Default.Shield,
                        accentColor = TidePrimaryLuminous,
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    MetricCard(
                        title = "Current Streak",
                        value = "${state.streak.currentStreak} d",
                        subtitle = "Best: ${state.streak.longestStreak} days",
                        icon = Icons.AutoMirrored.Filled.TrendingUp,
                        accentColor = TidePrimaryLuminous,
                        modifier = Modifier.weight(1f)
                    )
                    MetricCard(
                        title = "Clean Days",
                        value = "${state.streak.totalCleanDays}",
                        subtitle = "${state.streak.successRate}% rate",
                        icon = Icons.Default.Spa,
                        accentColor = TideTheme.colors.warning,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
            "REDUCE" -> {
                val target = state.goal?.targetAmount ?: 0.0
                val remainingAllowance = max(0.0, target - state.todayConsumptionCount)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    MetricCard(
                        title = "Money Saved",
                        value = "₹${state.money.lifetimeSaved.toInt()}",
                        subtitle = "₹${state.money.todaySaved.toInt()} today",
                        icon = Icons.Default.LocalAtm,
                        accentColor = TideSuccess,
                        modifier = Modifier.weight(1f)
                    )
                    MetricCard(
                        title = "Allowance Left",
                        value = "${remainingAllowance.toInt()} ${addiction.unit}",
                        subtitle = "Target: ${target.toInt()} / day",
                        icon = Icons.Default.Shield,
                        accentColor = if (remainingAllowance > 0) TidePrimaryLuminous else TideSlip,
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    MetricCard(
                        title = "Target Streak",
                        value = "${state.streak.currentStreak} d",
                        subtitle = "Best: ${state.streak.longestStreak} d on target",
                        icon = Icons.AutoMirrored.Filled.TrendingUp,
                        accentColor = TidePrimaryLuminous,
                        modifier = Modifier.weight(1f)
                    )
                    MetricCard(
                        title = "Days on Target",
                        value = "${state.streak.totalCleanDays}",
                        subtitle = "${state.streak.successRate}% within target",
                        icon = Icons.Default.DoneAll,
                        accentColor = TideTheme.colors.warning,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
            else -> { // "TRACK"
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    MetricCard(
                        title = "Money Spent",
                        value = "₹${state.money.lifetimeSpent.toInt()}",
                        subtitle = "₹${state.money.todaySpent.toInt()} today",
                        icon = Icons.Default.LocalAtm,
                        accentColor = TidePrimaryLuminous,
                        modifier = Modifier.weight(1f)
                    )
                    MetricCard(
                        title = "Logged Today",
                        value = "${state.todayConsumptionCount.toInt()} ${addiction.unit}",
                        subtitle = "Baseline: ${addiction.baselineAmount.toInt()} ${addiction.unit}",
                        icon = Icons.Default.Equalizer,
                        accentColor = TidePrimaryLuminous,
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    MetricCard(
                        title = "Days Tracked",
                        value = "${state.streak.totalElapsedDays} d",
                        subtitle = "Active observation",
                        icon = Icons.AutoMirrored.Filled.TrendingUp,
                        accentColor = TidePrimaryLuminous,
                        modifier = Modifier.weight(1f)
                    )
                    MetricCard(
                        title = "Today's Logs",
                        value = "${state.todayEvents.size}",
                        subtitle = if (state.todayCheckIn != null) "Checked in today" else "No check-in yet",
                        icon = Icons.Default.Spa,
                        accentColor = TideTheme.colors.warning,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Daily Check-In Prompt Card
        DailyCheckInCard(
            checkIn = state.todayCheckIn,
            onCheckInClick = { showCheckInSheet = true }
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Recent Today Timeline
        Text(
            text = "Today's Activity",
            style = MaterialTheme.typography.titleMedium,
            color = TideTheme.colors.textPrimary
        )
        Spacer(modifier = Modifier.height(10.dp))

        if (state.todayEvents.isEmpty()) {
            TideCard {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Outlined.CheckCircle,
                        contentDescription = null,
                        tint = TideSuccess,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Calm waters today",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium,
                            color = TideTheme.colors.textPrimary
                        )
                        Text(
                            text = when (addiction.goalType.uppercase()) {
                                "QUIT" -> "No consumption or slips recorded today."
                                "REDUCE" -> "Within target. No logs recorded yet today."
                                else -> "No activity recorded yet today. Tap + to log."
                            },
                            style = MaterialTheme.typography.bodySmall,
                            color = TideTheme.colors.textSecondary
                        )
                    }
                }
            }
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                state.todayEvents.forEach { event ->
                    TodayEventRow(event = event)
                }
            }
        }
    }

    // Modal Action Sheets
    if (showQuickLogSheet) {
        QuickLogBottomSheet(
            onDismiss = onDismissQuickLogSheet,
            onSelectConsumption = { showConsumptionSheet = true },
            onSelectCraving = { showCravingSheet = true },
            onSelectSlip = { showSlipSheet = true },
            onSelectCheckIn = { showCheckInSheet = true }
        )
    }

    if (showConsumptionSheet) {
        LogConsumptionBottomSheet(
            unit = addiction.unit,
            triggers = state.triggers,
            onDismiss = { showConsumptionSheet = false },
            onSave = { amount, triggerId, note ->
                viewModel.logConsumption(amount, triggerId, note)
            }
        )
    }

    if (showCravingSheet) {
        LogCravingBottomSheet(
            triggers = state.triggers,
            onDismiss = { showCravingSheet = false },
            onSave = { intensity, triggerId, mood, resisted, note ->
                viewModel.logCraving(intensity, triggerId, mood, resisted, note)
            }
        )
    }

    if (showSlipSheet) {
        LogSlipBottomSheet(
            unit = addiction.unit,
            triggers = state.triggers,
            onDismiss = { showSlipSheet = false },
            onSave = { amount, triggerId, mood, note ->
                viewModel.logSlip(amount, triggerId, mood, note)
            }
        )
    }

    if (showCheckInSheet) {
        DailyCheckInBottomSheet(
            onDismiss = { showCheckInSheet = false },
            onSave = { mood, hadCravings, intensity, note ->
                viewModel.saveCheckIn(mood, hadCravings, intensity, note)
            }
        )
    }
}

@Composable
private fun AdaptiveHeroCard(
    goalType: String,
    addictionName: String,
    unit: String,
    streakDays: Int,
    todayCount: Double,
    targetAmount: Double,
    elapsedDays: Int
) {
    TideGlassCard(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            when (goalType.uppercase()) {
                "QUIT" -> {
                    Text(
                        text = "$streakDays ${if (streakDays == 1) "day" else "days"}",
                        style = MaterialTheme.typography.displayLarge.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = (-1).sp
                        ),
                        color = TidePrimaryLuminous
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "since your last $addictionName",
                        style = MaterialTheme.typography.titleMedium,
                        color = TideTheme.colors.textSecondary
                    )
                }
                "REDUCE" -> {
                    Text(
                        text = "${todayCount.toInt()} / ${targetAmount.toInt()}",
                        style = MaterialTheme.typography.displayLarge.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = (-1).sp
                        ),
                        color = if (todayCount <= targetAmount) TideSuccess else TideSlip
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Today's target ($unit)",
                        style = MaterialTheme.typography.titleMedium,
                        color = TideTheme.colors.textSecondary
                    )
                }
                else -> {
                    Text(
                        text = "Day $elapsedDays",
                        style = MaterialTheme.typography.displayLarge.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = (-1).sp
                        ),
                        color = TidePrimaryLuminous
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Understanding your pattern",
                        style = MaterialTheme.typography.titleMedium,
                        color = TideTheme.colors.textSecondary
                    )
                }
            }
        }
    }
}

@Composable
private fun DailyCheckInCard(
    checkIn: com.tide.app.data.local.entity.DailyCheckInEntity?,
    onCheckInClick: () -> Unit
) {
    TideCard(
        onClick = onCheckInClick
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    shape = CircleShape,
                    color = if (checkIn != null) TideSuccess.copy(alpha = 0.15f) else TidePrimary.copy(alpha = 0.15f),
                    modifier = Modifier.size(36.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = if (checkIn != null) Icons.Default.Check else Icons.Default.Mood,
                            contentDescription = null,
                            tint = if (checkIn != null) TideSuccess else TidePrimaryLuminous,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = if (checkIn != null) "Today's Check-In Complete" else "Daily Check-In",
                        style = MaterialTheme.typography.titleSmall,
                        color = TideTheme.colors.textPrimary
                    )
                    Text(
                        text = if (checkIn != null) "Mood: ${checkIn.mood}" else "10 seconds to reflect on your day",
                        style = MaterialTheme.typography.bodySmall,
                        color = TideTheme.colors.textSecondary
                    )
                }
            }

            Text(
                text = if (checkIn != null) "Update" else "Start →",
                style = MaterialTheme.typography.labelSmall,
                color = TidePrimaryLuminous
            )
        }
    }
}

@Composable
private fun TodayEventRow(event: TodayLogEvent) {
    val timeStr = Instant.ofEpochMilli(event.timestamp)
        .atZone(ZoneId.systemDefault())
        .format(DateTimeFormatter.ofPattern("h:mm a"))

    val icon = when (event.type) {
        "CONSUMPTION" -> Icons.Default.Add
        "CRAVING" -> Icons.Default.Air
        "SLIP" -> Icons.Outlined.Warning
        else -> Icons.Default.Check
    }

    val iconColor = when (event.type) {
        "CONSUMPTION" -> TidePrimaryLuminous
        "CRAVING" -> TideTheme.colors.warning
        "SLIP" -> TideSlip
        else -> TideSuccess
    }

    TideCard {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    shape = CircleShape,
                    color = iconColor.copy(alpha = 0.15f),
                    modifier = Modifier.size(32.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(icon, contentDescription = null, tint = iconColor, modifier = Modifier.size(16.dp))
                    }
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(event.title, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium, color = TideTheme.colors.textPrimary)
                    Text(event.detail, style = MaterialTheme.typography.bodySmall, color = TideTheme.colors.textSecondary)
                }
            }
            Text(timeStr, style = MaterialTheme.typography.labelSmall, color = TideTheme.colors.textTertiary)
        }
    }
}
