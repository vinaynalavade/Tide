package com.tide.app.ui.habits

import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Restore
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tide.app.core.designsystem.TideCard
import com.tide.app.core.theme.TidePrimaryLuminous
import com.tide.app.core.theme.TideSlip
import com.tide.app.core.theme.TideSuccess
import com.tide.app.core.theme.TideTheme
import com.tide.app.data.local.entity.AddictionEntity
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArchivedHabitsBottomSheet(
    archivedHabits: List<AddictionEntity>,
    onDismiss: () -> Unit,
    onRestoreHabit: (Long) -> Unit,
    onDeleteHabit: (Long) -> Unit
) {
    var habitToDelete by remember { mutableStateOf<AddictionEntity?>(null) }

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
            Text(
                text = "Archived Habits",
                style = MaterialTheme.typography.titleLarge,
                color = TideTheme.colors.textPrimary
            )
            Text(
                text = "Archived habits are paused from your daily dashboard. All historical records and milestones are safely preserved.",
                style = MaterialTheme.typography.bodySmall,
                color = TideTheme.colors.textSecondary
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (archivedHabits.isEmpty()) {
                TideCard {
                    Text(
                        text = "No archived habits.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TideTheme.colors.textSecondary
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(archivedHabits, key = { it.id }) { habit ->
                        val dateStr = habit.archivedAt?.let {
                            Instant.ofEpochMilli(it).atZone(ZoneId.systemDefault()).format(DateTimeFormatter.ofPattern("MMM d, yyyy"))
                        } ?: "Archived"

                        TideCard {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = habit.name,
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.SemiBold,
                                        color = TideTheme.colors.textPrimary
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = "${habit.category} • Archived on $dateStr",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = TideTheme.colors.textSecondary
                                    )
                                }

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    IconButton(
                                        onClick = { onRestoreHabit(habit.id) },
                                        modifier = Modifier.size(36.dp)
                                    ) {
                                        Icon(Icons.Default.Restore, contentDescription = "Restore habit", tint = TideSuccess, modifier = Modifier.size(20.dp))
                                    }
                                    IconButton(
                                        onClick = { habitToDelete = habit },
                                        modifier = Modifier.size(36.dp)
                                    ) {
                                        Icon(Icons.Default.Delete, contentDescription = "Delete permanently", tint = TideSlip, modifier = Modifier.size(20.dp))
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    habitToDelete?.let { habit ->
        AlertDialog(
            onDismissRequest = { habitToDelete = null },
            title = { Text("Permanently Delete ${habit.name}?", color = TideSlip) },
            text = {
                Text("This will permanently remove ${habit.name} and all its tracking history. This action cannot be undone.")
            },
            confirmButton = {
                TextButton(onClick = {
                    onDeleteHabit(habit.id)
                    habitToDelete = null
                }) {
                    Text("Delete Permanently", color = TideSlip, fontWeight = FontWeight.Bold)
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
