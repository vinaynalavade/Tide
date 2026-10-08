package com.tide.app.ui.habits

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Archive
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.History
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
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
import com.tide.app.core.designsystem.TideCard
import com.tide.app.core.designsystem.TidePrimaryButton
import com.tide.app.core.theme.TidePrimary
import com.tide.app.core.theme.TidePrimaryLuminous
import com.tide.app.core.theme.TideSlip
import com.tide.app.core.theme.TideSuccess
import com.tide.app.core.theme.TideTheme
import com.tide.app.data.local.entity.AddictionEntity

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManageHabitsBottomSheet(
    activeHabits: List<AddictionEntity>,
    onDismiss: () -> Unit,
    onAddNewHabit: () -> Unit,
    onEditHabit: (AddictionEntity) -> Unit,
    onArchiveHabit: (Long) -> Unit,
    onDeleteHabit: (Long) -> Unit,
    onViewArchived: () -> Unit
) {
    var habitToDelete by remember { mutableStateOf<AddictionEntity?>(null) }
    var habitToArchive by remember { mutableStateOf<AddictionEntity?>(null) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = TideTheme.colors.cardBackground,
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(top = 8.dp, bottom = 40.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Manage Habits",
                        style = MaterialTheme.typography.titleLarge,
                        color = TideTheme.colors.textPrimary
                    )
                    Text(
                        text = "${activeHabits.size} active habit${if (activeHabits.size != 1) "s" else ""}",
                        style = MaterialTheme.typography.bodySmall,
                        color = TideTheme.colors.textSecondary
                    )
                }

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = TidePrimary.copy(alpha = 0.15f),
                    border = BorderStroke(1.dp, TidePrimaryLuminous.copy(alpha = 0.5f)),
                    modifier = Modifier.clickable { onViewArchived() }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Archive, contentDescription = null, tint = TidePrimaryLuminous, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Archived", fontSize = 12.sp, color = TidePrimaryLuminous, fontWeight = FontWeight.SemiBold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (activeHabits.isEmpty()) {
                TideCard {
                    Text(
                        text = "No active habits right now. Tap below to add your first habit.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TideTheme.colors.textSecondary
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f, fill = false),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(activeHabits, key = { it.id }) { habit ->
                        HabitManageRow(
                            habit = habit,
                            onEdit = { onEditHabit(habit) },
                            onArchive = { habitToArchive = habit },
                            onDelete = { habitToDelete = habit }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            TidePrimaryButton(
                text = "+ Add New Habit",
                onClick = onAddNewHabit
            )
        }
    }

    // Confirmation dialog for Archiving
    habitToArchive?.let { habit ->
        AlertDialog(
            onDismissRequest = { habitToArchive = null },
            title = { Text("Archive ${habit.name}?") },
            text = {
                Text("This habit will be hidden from your active dashboard. All past logs, streaks, and insights will be preserved and can be restored anytime.")
            },
            confirmButton = {
                TextButton(onClick = {
                    onArchiveHabit(habit.id)
                    habitToArchive = null
                }) {
                    Text("Archive", fontWeight = FontWeight.Bold, color = TidePrimaryLuminous)
                }
            },
            dismissButton = {
                TextButton(onClick = { habitToArchive = null }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Confirmation dialog for Deleting
    habitToDelete?.let { habit ->
        AlertDialog(
            onDismissRequest = { habitToDelete = null },
            title = { Text("Delete ${habit.name}?", color = TideSlip) },
            text = {
                Text("Are you sure? This will permanently delete ${habit.name} and all its recorded events. Consider archiving instead if you want to keep historical records.")
            },
            confirmButton = {
                TextButton(onClick = {
                    onDeleteHabit(habit.id)
                    habitToDelete = null
                }) {
                    Text("Delete Permanently", fontWeight = FontWeight.Bold, color = TideSlip)
                }
            },
            dismissButton = {
                TextButton(onClick = { habitToDelete = null }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun HabitManageRow(
    habit: AddictionEntity,
    onEdit: () -> Unit,
    onArchive: () -> Unit,
    onDelete: () -> Unit
) {
    val goalColor = when (habit.goalType.uppercase()) {
        "QUIT" -> TideSlip
        "REDUCE" -> TideSuccess
        else -> TidePrimaryLuminous
    }

    TideCard {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = habit.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = TideTheme.colors.textPrimary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = goalColor.copy(alpha = 0.15f)
                    ) {
                        Text(
                            text = habit.goalType,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = goalColor,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "${habit.category} • Baseline: ${habit.baselineAmount.toInt()} ${habit.unit}/${habit.baselineFrequency}${if (habit.hasFinancialImpact) " • ₹${habit.baselineCost.toInt()}/day" else ""}",
                    style = MaterialTheme.typography.bodySmall,
                    color = TideTheme.colors.textSecondary
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onEdit, modifier = Modifier.size(36.dp)) {
                    Icon(Icons.Default.Edit, contentDescription = "Edit habit", tint = TidePrimaryLuminous, modifier = Modifier.size(18.dp))
                }
                IconButton(onClick = onArchive, modifier = Modifier.size(36.dp)) {
                    Icon(Icons.Default.Archive, contentDescription = "Archive habit", tint = TideTheme.colors.textSecondary, modifier = Modifier.size(18.dp))
                }
                IconButton(onClick = onDelete, modifier = Modifier.size(36.dp)) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete habit", tint = TideSlip, modifier = Modifier.size(18.dp))
                }
            }
        }
    }
}
