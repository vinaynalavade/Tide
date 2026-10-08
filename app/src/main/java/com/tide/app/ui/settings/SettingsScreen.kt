package com.tide.app.ui.settings

import android.app.Activity
import android.widget.Toast
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Archive
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material.icons.filled.Widgets
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tide.app.core.designsystem.TideCard
import com.tide.app.core.designsystem.TidePrimaryButton
import com.tide.app.core.designsystem.TideTopBar
import com.tide.app.core.theme.TidePrimary
import com.tide.app.core.theme.TidePrimaryLuminous
import com.tide.app.core.theme.TideSlip
import com.tide.app.core.theme.TideSuccess
import com.tide.app.core.theme.TideTheme
import com.tide.app.core.update.UpdateState
import com.tide.app.data.local.entity.AddictionEntity
import com.tide.app.ui.habits.AddEditHabitBottomSheet
import com.tide.app.ui.habits.ArchivedHabitsBottomSheet
import com.tide.app.ui.habits.ManageHabitsBottomSheet

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel,
    onResetToOnboarding: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()
    val updateState by viewModel.updateState.collectAsState()
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current

    var showDeleteConfirmDialog by remember { mutableStateOf(false) }
    var showLicensesDialog by remember { mutableStateOf(false) }

    // Multi-habit management bottom sheets
    var showManageHabitsSheet by remember { mutableStateOf(false) }
    var showAddEditHabitSheet by remember { mutableStateOf(false) }
    var habitToEdit by remember { mutableStateOf<AddictionEntity?>(null) }
    var showArchivedHabitsSheet by remember { mutableStateOf(false) }

    if (state.isDataDeleted) {
        onResetToOnboarding()
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(bottom = 100.dp)
    ) {
        TideTopBar(
            title = "Settings",
            subtitle = "Preferences, habits & data management"
        )

        // ==========================================
        // SECTION 1: MY HABITS
        // ==========================================
        Column(modifier = Modifier.padding(horizontal = 20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "My Habits",
                    style = MaterialTheme.typography.titleMedium,
                    color = TideTheme.colors.textPrimary
                )
                Text(
                    text = "${state.activeHabits.size} Active",
                    style = MaterialTheme.typography.labelSmall,
                    color = TidePrimaryLuminous
                )
            }
            Spacer(modifier = Modifier.height(10.dp))

            TideCard {
                Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    // Quick Action: Manage Habits
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showManageHabitsSheet = true },
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.List, contentDescription = null, tint = TidePrimaryLuminous, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text("Manage Habits", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold, color = TideTheme.colors.textPrimary)
                                Text("Edit configuration, archive, or reorder", style = MaterialTheme.typography.bodySmall, color = TideTheme.colors.textSecondary)
                            }
                        }
                        Text("Manage →", style = MaterialTheme.typography.labelSmall, color = TidePrimaryLuminous)
                    }

                    // Quick Action: Add Habit
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                habitToEdit = null
                                showAddEditHabitSheet = true
                            },
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Add, contentDescription = null, tint = TideSuccess, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text("Add Habit", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold, color = TideTheme.colors.textPrimary)
                                Text("Smoking, Sugar, Gaming, Custom & more", style = MaterialTheme.typography.bodySmall, color = TideTheme.colors.textSecondary)
                            }
                        }
                        Text("Add +", style = MaterialTheme.typography.labelSmall, color = TideSuccess)
                    }

                    // Quick Action: Archived Habits
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showArchivedHabitsSheet = true },
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Archive, contentDescription = null, tint = TideTheme.colors.textSecondary, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text("Archived Habits", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold, color = TideTheme.colors.textPrimary)
                                Text("${state.archivedHabits.size} archived", style = MaterialTheme.typography.bodySmall, color = TideTheme.colors.textSecondary)
                            }
                        }
                        Text("View →", style = MaterialTheme.typography.labelSmall, color = TideTheme.colors.textSecondary)
                    }

                    // Active Habit Summary List
                    if (state.activeHabits.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Active Habit Configurations",
                            style = MaterialTheme.typography.labelMedium,
                            color = TideTheme.colors.textSecondary
                        )

                        state.activeHabits.forEach { habit ->
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = TideTheme.colors.cardBackground.copy(alpha = 0.5f),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(habit.name, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold, color = TideTheme.colors.textPrimary)
                                        Text(
                                            "${habit.goalType} • ${habit.unit} • ${if (habit.hasFinancialImpact) "Financial" else "Non-Financial"}",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = TideTheme.colors.textSecondary
                                        )
                                    }
                                    IconButton(
                                        onClick = {
                                            habitToEdit = habit
                                            showAddEditHabitSheet = true
                                        },
                                        modifier = Modifier.size(32.dp)
                                    ) {
                                        Icon(Icons.Default.Edit, contentDescription = "Edit habit", tint = TidePrimaryLuminous, modifier = Modifier.size(18.dp))
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // ==========================================
        // SECTION 2: GOALS & TARGETS
        // ==========================================
        Column(modifier = Modifier.padding(horizontal = 20.dp)) {
            Text(
                text = "Goals & Targets",
                style = MaterialTheme.typography.titleMedium,
                color = TideTheme.colors.textPrimary
            )
            Spacer(modifier = Modifier.height(10.dp))

            TideCard {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(verticalAlignment = Alignment.Top) {
                        Icon(Icons.Default.Flag, contentDescription = null, tint = TidePrimaryLuminous, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text("Universal Goal Architecture", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold, color = TideTheme.colors.textPrimary)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                "Tide supports TRACK, REDUCE, and QUIT modes. Goal transitions are effective from today onward — historical streaks, milestones, and past logs remain immutable.",
                                style = MaterialTheme.typography.bodySmall,
                                color = TideTheme.colors.textSecondary
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // ==========================================
        // SECTION 3: TRACKING & WIDGET
        // ==========================================
        Column(modifier = Modifier.padding(horizontal = 20.dp)) {
            Text(
                text = "Tracking & Quick Actions",
                style = MaterialTheme.typography.titleMedium,
                color = TideTheme.colors.textPrimary
            )
            Spacer(modifier = Modifier.height(10.dp))

            TideCard {
                Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    // Home-Screen Widget Info
                    Row(verticalAlignment = Alignment.Top) {
                        Icon(Icons.Default.Widgets, contentDescription = null, tint = TidePrimaryLuminous, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text("Android Home-Screen Widget", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold, color = TideTheme.colors.textPrimary)
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                "Log consumption, capture cravings, undo recent logs, and cycle habits directly from your Android home-screen widget without launching the full application.",
                                style = MaterialTheme.typography.bodySmall,
                                color = TideTheme.colors.textSecondary
                            )
                        }
                    }

                    // Haptics toggle
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Vibration, contentDescription = null, tint = TidePrimaryLuminous, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(10.dp))
                            Text("Haptic Feedback", style = MaterialTheme.typography.bodyMedium, color = TideTheme.colors.textPrimary)
                        }
                        Switch(
                            checked = state.userSettings.hapticsEnabled,
                            onCheckedChange = { viewModel.toggleHaptics(it) },
                            colors = SwitchDefaults.colors(checkedThumbColor = TidePrimaryLuminous, checkedTrackColor = TidePrimary)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // ==========================================
        // SECTION 4: NOTIFICATIONS
        // ==========================================
        Column(modifier = Modifier.padding(horizontal = 20.dp)) {
            Text(
                text = "Notifications",
                style = MaterialTheme.typography.titleMedium,
                color = TideTheme.colors.textPrimary
            )
            Spacer(modifier = Modifier.height(10.dp))

            TideCard {
                Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    state.notificationPreferences.forEach { pref ->
                        val title = when (pref.type) {
                            "daily_checkin" -> "Daily Check-In Reminder (${pref.time})"
                            "goal_reminder" -> "Goal Progress Reminder (${pref.time})"
                            "milestone" -> "Milestone Celebrations"
                            else -> "High-Risk Awareness Reminders"
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(title, style = MaterialTheme.typography.bodyMedium, color = TideTheme.colors.textPrimary)
                            Switch(
                                checked = pref.enabled,
                                onCheckedChange = { viewModel.toggleNotificationPreference(pref) },
                                colors = SwitchDefaults.colors(checkedThumbColor = TidePrimaryLuminous, checkedTrackColor = TidePrimary)
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // ==========================================
        // SECTION 5: APPEARANCE
        // ==========================================
        Column(modifier = Modifier.padding(horizontal = 20.dp)) {
            Text(
                text = "Appearance",
                style = MaterialTheme.typography.titleMedium,
                color = TideTheme.colors.textPrimary
            )
            Spacer(modifier = Modifier.height(10.dp))

            TideCard {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Palette, contentDescription = null, tint = TidePrimaryLuminous, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(10.dp))
                        Text("Theme", style = MaterialTheme.typography.bodyMedium, color = TideTheme.colors.textPrimary)
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        listOf("system", "light", "dark").forEach { mode ->
                            val isSelected = state.userSettings.themeMode == mode
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (isSelected) TidePrimary.copy(alpha = 0.15f) else Color.Transparent,
                                border = BorderStroke(1.dp, if (isSelected) TidePrimaryLuminous else TideTheme.colors.cardBorder),
                                modifier = Modifier.clickable { viewModel.setTheme(mode) }
                            ) {
                                Text(
                                    text = mode.replaceFirstChar { it.uppercase() },
                                    fontSize = 11.sp,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = if (isSelected) TidePrimaryLuminous else TideTheme.colors.textSecondary,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // ==========================================
        // SECTION 6: ACCOUNT & ARCHITECTURE
        // ==========================================
        Column(modifier = Modifier.padding(horizontal = 20.dp)) {
            Text(
                text = "Account & Sync",
                style = MaterialTheme.typography.titleMedium,
                color = TideTheme.colors.textPrimary
            )
            Spacer(modifier = Modifier.height(10.dp))

            TideCard {
                Row(verticalAlignment = Alignment.Top) {
                    Icon(Icons.Default.Person, contentDescription = null, tint = TidePrimaryLuminous, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text("Local-First Architecture", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold, color = TideTheme.colors.textPrimary)
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            "Status: Offline Local Storage (100% Private)\nAll tracking and configurations are stored directly on this device. Future cloud sync and cross-device backup architecture are prepared without compromising local speed or privacy.",
                            style = MaterialTheme.typography.bodySmall,
                            color = TideTheme.colors.textSecondary
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // ==========================================
        // SECTION 7: PRIVACY & DATA
        // ==========================================
        Column(modifier = Modifier.padding(horizontal = 20.dp)) {
            Text(
                text = "Privacy & Data",
                style = MaterialTheme.typography.titleMedium,
                color = TideTheme.colors.textPrimary
            )
            Spacer(modifier = Modifier.height(10.dp))

            TideCard {
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    // Privacy notice
                    Row(verticalAlignment = Alignment.Top) {
                        Icon(Icons.Default.Security, contentDescription = null, tint = TideSuccess, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text("100% Private & Telemetry-Free", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold, color = TideTheme.colors.textPrimary)
                            Text(
                                "Your tracking data remains on your device. Tide does not sell your behavioral data or run telemetry trackers.",
                                style = MaterialTheme.typography.bodySmall,
                                color = TideTheme.colors.textSecondary
                            )
                        }
                    }

                    // Export data button
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { viewModel.exportData() },
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.FileDownload, contentDescription = null, tint = TidePrimaryLuminous, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(10.dp))
                            Text("Export All Habits Data (JSON)", style = MaterialTheme.typography.bodyMedium, color = TideTheme.colors.textPrimary)
                        }
                        Text("Export →", style = MaterialTheme.typography.labelSmall, color = TidePrimaryLuminous)
                    }

                    // Delete all data button
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showDeleteConfirmDialog = true },
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.DeleteForever, contentDescription = null, tint = TideSlip, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(10.dp))
                            Text("Delete All Data", style = MaterialTheme.typography.bodyMedium, color = TideSlip)
                        }
                        Text("Clear", style = MaterialTheme.typography.labelSmall, color = TideSlip)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // ==========================================
        // SECTION 8: APP UPDATES
        // ==========================================
        Column(modifier = Modifier.padding(horizontal = 20.dp)) {
            Text(
                text = "App Updates",
                style = MaterialTheme.typography.titleMedium,
                color = TideTheme.colors.textPrimary
            )
            Spacer(modifier = Modifier.height(10.dp))

            InAppUpdateSection(
                updateState = updateState,
                onCheckUpdates = { viewModel.checkForUpdates() },
                onStartUpdate = { (context as? Activity)?.let { viewModel.startUpdate(it) } },
                onCompleteUpdate = { viewModel.completeUpdate() }
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // ==========================================
        // SECTION 9: ABOUT
        // ==========================================
        Column(modifier = Modifier.padding(horizontal = 20.dp)) {
            Text(
                text = "About",
                style = MaterialTheme.typography.titleMedium,
                color = TideTheme.colors.textPrimary
            )
            Spacer(modifier = Modifier.height(10.dp))

            TideCard {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    JourneyItem(label = "Application", value = "Tide")
                    JourneyItem(label = "Version", value = com.tide.app.BuildConfig.VERSION_NAME)
                    JourneyItem(label = "Build", value = com.tide.app.BuildConfig.VERSION_CODE.toString())
                    JourneyItem(label = "Architecture", value = "Addiction-Agnostic Multi-Habit Platform")

                    Spacer(modifier = Modifier.height(4.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showLicensesDialog = true },
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Open Source Licenses", style = MaterialTheme.typography.bodySmall, color = TidePrimaryLuminous)
                        Text("View →", style = MaterialTheme.typography.labelSmall, color = TidePrimaryLuminous)
                    }
                }
            }
        }
    }

    // ==========================================
    // BOTTOM SHEETS: HABIT MANAGEMENT
    // ==========================================
    if (showManageHabitsSheet) {
        ManageHabitsBottomSheet(
            activeHabits = state.activeHabits,
            onDismiss = { showManageHabitsSheet = false },
            onAddNewHabit = {
                showManageHabitsSheet = false
                habitToEdit = null
                showAddEditHabitSheet = true
            },
            onEditHabit = { habit ->
                showManageHabitsSheet = false
                habitToEdit = habit
                showAddEditHabitSheet = true
            },
            onArchiveHabit = { habitId ->
                viewModel.archiveHabit(habitId)
            },
            onDeleteHabit = { habitId ->
                viewModel.deleteHabit(habitId)
            },
            onViewArchived = {
                showManageHabitsSheet = false
                showArchivedHabitsSheet = true
            }
        )
    }

    if (showAddEditHabitSheet) {
        AddEditHabitBottomSheet(
            habitToEdit = habitToEdit,
            onDismiss = {
                showAddEditHabitSheet = false
                habitToEdit = null
            },
            onSaveHabit = { habit, product, goal ->
                if (habitToEdit == null) {
                    viewModel.createHabit(habit, product, goal)
                } else {
                    viewModel.updateHabitConfig(habit, product, goal)
                }
                showAddEditHabitSheet = false
                habitToEdit = null
            }
        )
    }

    if (showArchivedHabitsSheet) {
        ArchivedHabitsBottomSheet(
            archivedHabits = state.archivedHabits,
            onDismiss = { showArchivedHabitsSheet = false },
            onRestoreHabit = { habitId ->
                viewModel.restoreHabit(habitId)
            },
            onDeleteHabit = { habitId ->
                viewModel.deleteHabit(habitId)
            }
        )
    }

    // EXPORT JSON DIALOG
    state.exportedJson?.let { jsonString ->
        AlertDialog(
            onDismissRequest = { viewModel.clearExportDialog() },
            title = { Text("Exported Data (v1.1.0)") },
            text = {
                Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                    Text(
                        text = jsonString,
                        fontSize = 11.sp,
                        fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    clipboardManager.setText(AnnotatedString(jsonString))
                    Toast.makeText(context, "Copied to clipboard", Toast.LENGTH_SHORT).show()
                    viewModel.clearExportDialog()
                }) {
                    Text("Copy to Clipboard")
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.clearExportDialog() }) {
                    Text("Close")
                }
            }
        )
    }

    // DELETE CONFIRMATION DIALOG
    if (showDeleteConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirmDialog = false },
            title = { Text("Delete All Data?", color = TideSlip) },
            text = {
                Text("This action will permanently delete all your tracking entries, habits, cravings, check-ins, and goals. This cannot be undone.")
            },
            confirmButton = {
                TextButton(onClick = {
                    showDeleteConfirmDialog = false
                    viewModel.deleteAllData()
                }) {
                    Text("Yes, Delete Everything", color = TideSlip, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirmDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // OPEN SOURCE LICENSES DIALOG
    if (showLicensesDialog) {
        AlertDialog(
            onDismissRequest = { showLicensesDialog = false },
            title = { Text("Licenses & Attribution") },
            text = {
                Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                    Text("Tide is built with modern open-source Android libraries:\n\n• Jetpack Compose & Material 3 (Apache 2.0)\n• AndroidX Room Persistence (Apache 2.0)\n• Google Play In-App Update API\n• Kotlin Coroutines & Flow (Apache 2.0)\n\nCreated with care for personal recovery and mindful habit transformation.")
                }
            },
            confirmButton = {
                TextButton(onClick = { showLicensesDialog = false }) {
                    Text("Done")
                }
            }
        )
    }
}

@Composable
private fun InAppUpdateSection(
    updateState: UpdateState,
    onCheckUpdates: () -> Unit,
    onStartUpdate: () -> Unit,
    onCompleteUpdate: () -> Unit
) {
    TideCard {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.CloudDownload,
                        contentDescription = null,
                        tint = TidePrimaryLuminous,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text("Google Play In-App Update", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium, color = TideTheme.colors.textPrimary)
                }

                if (updateState is UpdateState.Checking) {
                    CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp, color = TidePrimary)
                } else {
                    IconButton(onClick = onCheckUpdates, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Default.Refresh, contentDescription = "Check for updates", tint = TidePrimaryLuminous, modifier = Modifier.size(18.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            when (updateState) {
                is UpdateState.Idle, is UpdateState.UpToDate -> {
                    Text(
                        text = "Tide is up to date (Version ${com.tide.app.BuildConfig.VERSION_NAME}, Build ${com.tide.app.BuildConfig.VERSION_CODE}).",
                        style = MaterialTheme.typography.bodySmall,
                        color = TideTheme.colors.textSecondary
                    )
                }
                is UpdateState.Checking -> {
                    Text(
                        text = "Checking Google Play Store for new updates...",
                        style = MaterialTheme.typography.bodySmall,
                        color = TideTheme.colors.textSecondary
                    )
                }
                is UpdateState.Available -> {
                    Column {
                        Text(
                            text = "Tide ${updateState.availableVersionName} is available.",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = TideSuccess
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "What's improved:",
                            style = MaterialTheme.typography.labelSmall,
                            color = TideTheme.colors.textPrimary
                        )
                        updateState.releaseNotes.forEach { note ->
                            Text("• $note", style = MaterialTheme.typography.bodySmall, color = TideTheme.colors.textSecondary)
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        TidePrimaryButton(
                            text = "Update Now",
                            onClick = onStartUpdate
                        )
                    }
                }
                is UpdateState.Downloading -> {
                    Column {
                        Text(
                            text = "Downloading update... ${updateState.percent}%",
                            style = MaterialTheme.typography.bodySmall,
                            color = TidePrimaryLuminous
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        LinearProgressIndicator(
                            progress = { updateState.percent / 100f },
                            modifier = Modifier.fillMaxWidth().height(6.dp),
                            color = TidePrimaryLuminous
                        )
                    }
                }
                is UpdateState.Downloaded -> {
                    Column {
                        Text(
                            text = "Update downloaded and ready to install.",
                            style = MaterialTheme.typography.bodySmall,
                            color = TideSuccess
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        TidePrimaryButton(
                            text = "Install & Restart",
                            onClick = onCompleteUpdate
                        )
                    }
                }
                is UpdateState.Completed -> {
                    Text(
                        text = "Update completed successfully.",
                        style = MaterialTheme.typography.bodySmall,
                        color = TideSuccess
                    )
                }
                is UpdateState.Error -> {
                    Text(
                        text = updateState.message,
                        style = MaterialTheme.typography.bodySmall,
                        color = TideSlip
                    )
                }
                is UpdateState.Offline -> {
                    Text(
                        text = "You're offline. Your tracking continues normally.",
                        style = MaterialTheme.typography.bodySmall,
                        color = TideTheme.colors.textSecondary
                    )
                }
            }
        }
    }
}

@Composable
private fun JourneyItem(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, style = MaterialTheme.typography.bodySmall, color = TideTheme.colors.textSecondary)
        Text(text = value, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Medium, color = TideTheme.colors.textPrimary)
    }
}
