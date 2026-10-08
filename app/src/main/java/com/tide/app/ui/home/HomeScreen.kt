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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Air
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Equalizer
import androidx.compose.material.icons.filled.LocalAtm
import androidx.compose.material.icons.filled.Mood
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material.icons.filled.Undo
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import com.tide.app.data.local.entity.AddictionEntity
import com.tide.app.ui.habits.AddEditHabitBottomSheet
import com.tide.app.ui.habits.ArchivedHabitsBottomSheet
import com.tide.app.ui.habits.ManageHabitsBottomSheet
import com.tide.app.ui.tracking.DailyCheckInBottomSheet
import com.tide.app.ui.tracking.LogConsumptionBottomSheet
import com.tide.app.ui.tracking.LogCravingBottomSheet
import com.tide.app.ui.tracking.LogSlipBottomSheet
import com.tide.app.ui.tracking.QuickLogBottomSheet
import java.time.Instant
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import kotlin.math.max

@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onNavigateToCravingMode: () -> Unit,
    showQuickLogSheet: Boolean = false,
    onDismissQuickLogSheet: () -> Unit = {}
) {
    val state by viewModel.uiState.collectAsState()

    var showAddHabitSheet by remember { mutableStateOf(false) }
    var habitToEdit by remember { mutableStateOf<HabitCardData?>(null) }
    var showManageHabitsSheet by remember { mutableStateOf(false) }
    var showArchivedSheet by remember { mutableStateOf(false) }

    // Habit-specific logging sheets
    var loggingConsumptionHabit by remember { mutableStateOf<HabitCardData?>(null) }
    var loggingCravingHabit by remember { mutableStateOf<HabitCardData?>(null) }
    var loggingSlipHabit by remember { mutableStateOf<HabitCardData?>(null) }
    var showCheckInSheet by remember { mutableStateOf(false) }

    if (state.isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = TidePrimary)
        }
        return
    }

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
            .padding(top = 16.dp, bottom = 110.dp)
    ) {
        // Editorial Greeting & Add Habit Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
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
            }

            Surface(
                shape = RoundedCornerShape(12.dp),
                color = TidePrimary.copy(alpha = 0.15f),
                border = BorderStroke(1.dp, TidePrimaryLuminous.copy(alpha = 0.4f)),
                modifier = Modifier.clickable { showAddHabitSheet = true }
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, tint = TidePrimaryLuminous, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Add Habit", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = TidePrimaryLuminous)
                }
            }
        }

        // UNDO BANNER (Section 18)
        state.undoState?.let { undo ->
            Spacer(modifier = Modifier.height(14.dp))
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = TideTheme.colors.cardBackground,
                border = BorderStroke(1.dp, TidePrimaryLuminous.copy(alpha = 0.4f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Undo, contentDescription = null, tint = TidePrimaryLuminous, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(undo.message, style = MaterialTheme.typography.bodySmall, color = TideTheme.colors.textPrimary)
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "UNDO",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = TidePrimaryLuminous,
                            modifier = Modifier
                                .clickable { viewModel.undoLastAction() }
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                        IconButton(onClick = { viewModel.dismissUndo() }, modifier = Modifier.size(24.dp)) {
                            Icon(Icons.Default.Close, contentDescription = "Dismiss", tint = TideTheme.colors.textSecondary, modifier = Modifier.size(14.dp))
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Craving Mode Banner Button
        TideCravingBannerButton(onClick = onNavigateToCravingMode)

        Spacer(modifier = Modifier.height(20.dp))

        // SECTION: TODAY'S HABITS
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Today's Habits",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = TideTheme.colors.textPrimary
            )
            Text(
                text = "Manage (${state.activeHabits.size}) →",
                style = MaterialTheme.typography.labelSmall,
                color = TidePrimaryLuminous,
                modifier = Modifier.clickable { showManageHabitsSheet = true }
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        if (state.activeHabits.isEmpty()) {
            TideCard(
                onClick = { showAddHabitSheet = true }
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "No active habits tracked yet",
                        style = MaterialTheme.typography.titleSmall,
                        color = TideTheme.colors.textPrimary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Tap to add your first habit (Smoking, Sugar, Social Media, etc.)",
                        style = MaterialTheme.typography.bodySmall,
                        color = TideTheme.colors.textSecondary
                    )
                }
            }
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                state.activeHabits.forEach { cardData ->
                    HabitCard(
                        data = cardData,
                        onQuickLogOne = { viewModel.logConsumption(cardData.habit.id, 1.0, null, null) },
                        onLogCustom = { loggingConsumptionHabit = cardData },
                        onLogCraving = { loggingCravingHabit = cardData },
                        onLogSlip = { loggingSlipHabit = cardData },
                        onEdit = { habitToEdit = cardData }
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
            fontWeight = FontWeight.Bold,
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
                            text = "No events recorded yet today. Use the cards above or widget for quick actions.",
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

    // BOTTOM SHEETS & MODALS

    // Quick Log Sheet from Bottom Nav
    if (showQuickLogSheet) {
        val targetHabit = state.activeHabits.firstOrNull()
        QuickLogBottomSheet(
            onDismiss = onDismissQuickLogSheet,
            onSelectConsumption = { loggingConsumptionHabit = targetHabit },
            onSelectCraving = { loggingCravingHabit = targetHabit },
            onSelectSlip = { loggingSlipHabit = targetHabit },
            onSelectCheckIn = { showCheckInSheet = true }
        )
    }

    // Add New Habit Sheet
    if (showAddHabitSheet) {
        AddEditHabitBottomSheet(
            onDismiss = { showAddHabitSheet = false },
            onSaveHabit = { habit, product, goal ->
                viewModel.createHabit(habit, product, goal)
            }
        )
    }

    // Edit Existing Habit Sheet
    habitToEdit?.let { card ->
        AddEditHabitBottomSheet(
            habitToEdit = card.habit,
            existingProduct = card.product,
            existingGoal = card.goal,
            onDismiss = { habitToEdit = null },
            onSaveHabit = { habit, product, goal ->
                viewModel.updateHabit(habit, product, goal)
                habitToEdit = null
            }
        )
    }

    // Manage Habits Sheet
    if (showManageHabitsSheet) {
        ManageHabitsBottomSheet(
            activeHabits = state.activeHabits.map { it.habit },
            onDismiss = { showManageHabitsSheet = false },
            onAddNewHabit = {
                showManageHabitsSheet = false
                showAddHabitSheet = true
            },
            onEditHabit = { habit ->
                showManageHabitsSheet = false
                habitToEdit = state.activeHabits.find { it.habit.id == habit.id }
            },
            onArchiveHabit = { habitId ->
                viewModel.archiveHabit(habitId)
            },
            onDeleteHabit = { habitId ->
                viewModel.deleteHabit(habitId)
            },
            onViewArchived = {
                showManageHabitsSheet = false
                showArchivedSheet = true
            }
        )
    }

    // Archived Habits Sheet
    if (showArchivedSheet) {
        ArchivedHabitsBottomSheet(
            archivedHabits = state.archivedHabits,
            onDismiss = { showArchivedSheet = false },
            onRestoreHabit = { habitId ->
                viewModel.restoreHabit(habitId)
            },
            onDeleteHabit = { habitId ->
                viewModel.deleteHabit(habitId)
            }
        )
    }

    // Custom Consumption Sheet
    loggingConsumptionHabit?.let { card ->
        LogConsumptionBottomSheet(
            unit = card.habit.unit,
            triggers = state.triggers,
            onDismiss = { loggingConsumptionHabit = null },
            onSave = { amount, triggerId, note ->
                viewModel.logConsumption(card.habit.id, amount, triggerId, note)
                loggingConsumptionHabit = null
            }
        )
    }

    // Craving Sheet
    loggingCravingHabit?.let { card ->
        LogCravingBottomSheet(
            triggers = state.triggers,
            onDismiss = { loggingCravingHabit = null },
            onSave = { intensity, triggerId, mood, resisted, note ->
                viewModel.logCraving(card.habit.id, intensity, triggerId, mood, resisted, note)
                loggingCravingHabit = null
            }
        )
    }

    // Slip Sheet
    loggingSlipHabit?.let { card ->
        LogSlipBottomSheet(
            unit = card.habit.unit,
            triggers = state.triggers,
            onDismiss = { loggingSlipHabit = null },
            onSave = { amount, triggerId, mood, note ->
                viewModel.logSlip(card.habit.id, amount, triggerId, mood, note)
                loggingSlipHabit = null
            }
        )
    }

    // Daily Check-in Sheet
    if (showCheckInSheet) {
        DailyCheckInBottomSheet(
            onDismiss = { showCheckInSheet = false },
            onSave = { mood, hadCravings, intensity, note ->
                viewModel.saveCheckIn(mood, hadCravings, intensity, note)
                showCheckInSheet = false
            }
        )
    }
}

@Composable
private fun HabitCard(
    data: HabitCardData,
    onQuickLogOne: () -> Unit,
    onLogCustom: () -> Unit,
    onLogCraving: () -> Unit,
    onLogSlip: () -> Unit,
    onEdit: () -> Unit
) {
    val habit = data.habit
    val goalColor = when (habit.goalType.uppercase()) {
        "QUIT" -> TideSlip
        "REDUCE" -> TideSuccess
        else -> TidePrimaryLuminous
    }

    TideGlassCard(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            // Header: Title, Category, Goal badge, Edit
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = habit.name,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = TideTheme.colors.textPrimary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = goalColor.copy(alpha = 0.15f)
                    ) {
                        Text(
                            text = habit.goalType,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = goalColor,
                            modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.dp)
                        )
                    }
                }

                IconButton(onClick = onEdit, modifier = Modifier.size(28.dp)) {
                    Icon(Icons.Default.Edit, contentDescription = "Edit habit", tint = TideTheme.colors.textSecondary, modifier = Modifier.size(16.dp))
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Main Contextual Progress Row
            when (habit.goalType.uppercase()) {
                "REDUCE" -> {
                    val target = data.goal?.targetAmount ?: 0.0
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        Column {
                            Text(
                                text = "${data.todayConsumptionCount.toInt()} / ${target.toInt()} ${habit.unit}",
                                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                                color = if (data.isCleanToday) TideSuccess else TideSlip
                            )
                            Text(
                                text = "Today's target (${data.remainingAllowance.toInt()} remaining)",
                                style = MaterialTheme.typography.bodySmall,
                                color = TideTheme.colors.textSecondary
                            )
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = "${data.streak.currentStreak} d",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = TidePrimaryLuminous
                            )
                            Text(
                                text = "Target streak",
                                style = MaterialTheme.typography.labelSmall,
                                color = TideTheme.colors.textSecondary
                            )
                        }
                    }
                }
                "QUIT" -> {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        Column {
                            Text(
                                text = "${data.streak.currentStreak} ${if (data.streak.currentStreak == 1) "day" else "days"}",
                                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                                color = TidePrimaryLuminous
                            )
                            Text(
                                text = "Clean since start • Best: ${data.streak.longestStreak} d",
                                style = MaterialTheme.typography.bodySmall,
                                color = TideTheme.colors.textSecondary
                            )
                        }

                        if (habit.hasFinancialImpact) {
                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = "₹${data.money.lifetimeSaved.toInt()}",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = TideSuccess
                                )
                                Text(
                                    text = "Money saved",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = TideTheme.colors.textSecondary
                                )
                            }
                        } else {
                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = "${data.avoided.lifetimeAvoided.toInt()}",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = TidePrimaryLuminous
                                )
                                Text(
                                    text = "${habit.unit} avoided",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = TideTheme.colors.textSecondary
                                )
                            }
                        }
                    }
                }
                else -> { // TRACK
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        Column {
                            Text(
                                text = "${data.todayConsumptionCount.toInt()} ${habit.unit}",
                                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                                color = TidePrimaryLuminous
                            )
                            Text(
                                text = "Baseline: ${habit.baselineAmount.toInt()} / ${habit.baselineFrequency}",
                                style = MaterialTheme.typography.bodySmall,
                                color = TideTheme.colors.textSecondary
                            )
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = "${data.streak.totalElapsedDays} d",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = TidePrimaryLuminous
                            )
                            Text(
                                text = "Active observation",
                                style = MaterialTheme.typography.labelSmall,
                                color = TideTheme.colors.textSecondary
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Action Buttons Row: [+1 Log] [Craving] [Slip]
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = TidePrimary.copy(alpha = 0.2f),
                    border = BorderStroke(1.dp, TidePrimaryLuminous.copy(alpha = 0.5f)),
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onQuickLogOne() }
                ) {
                    Row(
                        modifier = Modifier.padding(vertical = 9.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, tint = TidePrimaryLuminous, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("+1 Log", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TidePrimaryLuminous)
                    }
                }

                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = TideTheme.colors.cardBorder.copy(alpha = 0.2f),
                    border = BorderStroke(1.dp, TideTheme.colors.cardBorder),
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onLogCraving() }
                ) {
                    Row(
                        modifier = Modifier.padding(vertical = 9.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Air, contentDescription = null, tint = TideTheme.colors.warning, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Craving", fontSize = 12.sp, fontWeight = FontWeight.Medium, color = TideTheme.colors.textPrimary)
                    }
                }

                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = TideTheme.colors.cardBorder.copy(alpha = 0.2f),
                    border = BorderStroke(1.dp, TideTheme.colors.cardBorder),
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onLogSlip() }
                ) {
                    Row(
                        modifier = Modifier.padding(vertical = 9.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Outlined.Warning, contentDescription = null, tint = TideSlip, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Slip", fontSize = 12.sp, fontWeight = FontWeight.Medium, color = TideSlip)
                    }
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
    TideCard(onClick = onCheckInClick) {
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
