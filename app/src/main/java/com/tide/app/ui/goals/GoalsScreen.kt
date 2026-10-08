package com.tide.app.ui.goals

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
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
import com.tide.app.data.local.entity.MilestoneEntity
import com.tide.app.data.local.entity.RecoveryGoalEntity
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@Composable
fun GoalsScreen(
    viewModel: GoalsViewModel,
    onNavigateToSettings: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()
    var showAddGoalSheet by remember { mutableStateOf(false) }
    var showChangeGoalSheet by remember { mutableStateOf(false) }

    if (state.isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = TidePrimary)
        }
        return
    }

    val selectedHabit = state.selectedHabit

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(bottom = 100.dp)
    ) {
        TideTopBar(
            title = "Goals",
            subtitle = "Targets, milestones & recovery"
        )

        // Multi-habit selector chips
        if (state.activeHabits.size > 1) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                state.activeHabits.forEach { habit ->
                    val isSelected = habit.id == selectedHabit?.id
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
            Spacer(modifier = Modifier.height(16.dp))
        }

        if (selectedHabit == null) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(40.dp),
                contentAlignment = Alignment.Center
            ) {
                Text("No active habits found. Add a habit to get started.", color = TideTheme.colors.textSecondary)
            }
            return
        }

        // Active Goal Summary Card
        Column(modifier = Modifier.padding(horizontal = 20.dp)) {
            Text(
                text = "${selectedHabit.name} Journey",
                style = MaterialTheme.typography.titleMedium,
                color = TideTheme.colors.textPrimary
            )
            Spacer(modifier = Modifier.height(10.dp))

            TideCard {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "${selectedHabit.goalType} Mode",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = when (selectedHabit.goalType.uppercase()) {
                                "QUIT" -> TideSlip
                                "REDUCE" -> TideSuccess
                                else -> TidePrimaryLuminous
                            }
                        )

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = TidePrimary.copy(alpha = 0.15f),
                            border = BorderStroke(1.dp, TidePrimaryLuminous.copy(alpha = 0.5f)),
                            modifier = Modifier.clickable { showChangeGoalSheet = true }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.Edit, contentDescription = null, tint = TidePrimaryLuminous, modifier = Modifier.size(12.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Change Goal", fontSize = 11.sp, color = TidePrimaryLuminous, fontWeight = FontWeight.SemiBold)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = when (selectedHabit.goalType.uppercase()) {
                            "QUIT" -> "Completely clean from ${selectedHabit.name}. Current clean streak: ${state.streak.currentStreak} days."
                            "REDUCE" -> "Daily target: ${state.activeGoal?.targetAmount?.toInt() ?: 0} ${selectedHabit.unit}/day. Baseline: ${selectedHabit.baselineAmount.toInt()} ${selectedHabit.unit}."
                            else -> "Observing and understanding usage patterns without pressure."
                        },
                        style = MaterialTheme.typography.bodyMedium,
                        color = TideTheme.colors.textSecondary
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(28.dp))

        // Recovery / Savings Goals (only shown when habit has financial impact)
        if (selectedHabit.hasFinancialImpact) {
            Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Savings Wishlist",
                        style = MaterialTheme.typography.titleMedium,
                        color = TideTheme.colors.textPrimary
                    )

                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = TidePrimary.copy(alpha = 0.15f),
                        border = BorderStroke(1.dp, TidePrimaryLuminous),
                        modifier = Modifier.clickable { showAddGoalSheet = true }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, tint = TidePrimaryLuminous, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Add Goal", style = MaterialTheme.typography.labelSmall, color = TidePrimaryLuminous)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                if (state.recoveryGoals.isEmpty()) {
                    TideCard {
                        Text(
                            text = "Turn avoided expenses into tangible rewards. Add your first goal like a vacation, new gadget, or investment.",
                            style = MaterialTheme.typography.bodySmall,
                            color = TideTheme.colors.textSecondary
                        )
                    }
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        state.recoveryGoals.forEach { goal ->
                            RecoveryGoalCard(
                                goal = goal,
                                totalSaved = state.money.lifetimeSaved,
                                onDelete = { viewModel.deleteRecoveryGoal(goal) }
                            )
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(28.dp))
        }

        // Milestones
        Column(modifier = Modifier.padding(horizontal = 20.dp)) {
            Text(
                text = "Milestones",
                style = MaterialTheme.typography.titleMedium,
                color = TideTheme.colors.textPrimary
            )
            Spacer(modifier = Modifier.height(10.dp))

            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                state.milestones.forEach { milestone ->
                    MilestoneCard(milestone = milestone)
                }
            }
        }
    }

    if (showAddGoalSheet) {
        AddRecoveryGoalBottomSheet(
            onDismiss = { showAddGoalSheet = false },
            onSave = { name, amount ->
                viewModel.addRecoveryGoal(name, amount)
            }
        )
    }

    if (showChangeGoalSheet && selectedHabit != null) {
        ChangeGoalBottomSheet(
            habitName = selectedHabit.name,
            currentGoalType = selectedHabit.goalType,
            currentTargetAmount = state.activeGoal?.targetAmount ?: 0.0,
            unit = selectedHabit.unit,
            onDismiss = { showChangeGoalSheet = false },
            onSave = { newGoal, newTarget ->
                viewModel.changeGoal(selectedHabit.id, newGoal, newTarget)
                showChangeGoalSheet = false
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ChangeGoalBottomSheet(
    habitName: String,
    currentGoalType: String,
    currentTargetAmount: Double,
    unit: String,
    onDismiss: () -> Unit,
    onSave: (String, Double) -> Unit
) {
    var selectedGoal by remember { mutableStateOf(currentGoalType) }
    var targetStr by remember { mutableStateOf(currentTargetAmount.toInt().toString()) }

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
            Text("Adjust Goal for $habitName", style = MaterialTheme.typography.titleLarge, color = TideTheme.colors.textPrimary)
            Text(
                "Changing your goal takes effect from now onward without altering your past achievements.",
                style = MaterialTheme.typography.bodySmall,
                color = TideTheme.colors.textSecondary
            )

            Spacer(modifier = Modifier.height(20.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf("QUIT", "REDUCE", "TRACK").forEach { mode ->
                    val isSelected = selectedGoal.equals(mode, ignoreCase = true)
                    val modeColor = when (mode) {
                        "QUIT" -> TideSlip
                        "REDUCE" -> TideSuccess
                        else -> TidePrimaryLuminous
                    }
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (isSelected) modeColor.copy(alpha = 0.15f) else TideTheme.colors.cardBorder.copy(alpha = 0.2f),
                        border = BorderStroke(1.dp, if (isSelected) modeColor else Color.Transparent),
                        modifier = Modifier
                            .weight(1f)
                            .clickable { selectedGoal = mode }
                    ) {
                        Column(
                            modifier = Modifier.padding(vertical = 12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = mode,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = if (isSelected) modeColor else TideTheme.colors.textPrimary
                            )
                        }
                    }
                }
            }

            if (selectedGoal.equals("REDUCE", ignoreCase = true)) {
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = targetStr,
                    onValueChange = { targetStr = it },
                    label = { Text("Daily Target Allowance ($unit)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = TideSuccess,
                        unfocusedBorderColor = TideTheme.colors.cardBorder
                    )
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            TidePrimaryButton(
                text = "Update Goal",
                onClick = {
                    val tgt = targetStr.toDoubleOrNull() ?: 0.0
                    onSave(selectedGoal, tgt)
                }
            )
        }
    }
}

@Composable
private fun RecoveryGoalCard(
    goal: RecoveryGoalEntity,
    totalSaved: Double,
    onDelete: () -> Unit
) {
    val progress = (totalSaved / goal.targetAmount).toFloat().coerceIn(0f, 1f)
    val fundedAmount = kotlin.math.min(totalSaved, goal.targetAmount)
    val isAchieved = totalSaved >= goal.targetAmount

    TideCard {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Savings,
                        contentDescription = null,
                        tint = if (isAchieved) TideSuccess else TidePrimaryLuminous,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = goal.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = TideTheme.colors.textPrimary
                    )
                }

                IconButton(onClick = onDelete, modifier = Modifier.size(28.dp)) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete goal",
                        tint = TideTheme.colors.textTertiary,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp),
                color = if (isAchieved) TideSuccess else TidePrimaryLuminous,
                trackColor = TideTheme.colors.cardBorder
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "₹${fundedAmount.toInt()} / ₹${goal.targetAmount.toInt()}",
                    style = MaterialTheme.typography.bodySmall,
                    color = TideTheme.colors.textSecondary
                )
                Text(
                    text = "${(progress * 100).toInt()}% funded",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = if (isAchieved) TideSuccess else TidePrimaryLuminous
                )
            }
        }
    }
}

@Composable
private fun MilestoneCard(
    milestone: MilestoneEntity
) {
    val isAchieved = milestone.achievedAt != null
    val dateAchievedStr = if (isAchieved) {
        Instant.ofEpochMilli(milestone.achievedAt!!)
            .atZone(ZoneId.systemDefault())
            .format(DateTimeFormatter.ofPattern("MMM d, yyyy"))
    } else null

    TideCard(
        borderColor = if (isAchieved) TideSuccess.copy(alpha = 0.4f) else TideTheme.colors.cardBorder,
        backgroundColor = if (isAchieved) TideSuccess.copy(alpha = 0.05f) else TideTheme.colors.cardBackground
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    shape = CircleShape,
                    color = if (isAchieved) TideSuccess.copy(alpha = 0.15f) else TideTheme.colors.cardBorder.copy(alpha = 0.3f),
                    modifier = Modifier.size(36.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = if (isAchieved) Icons.Default.Check else Icons.Default.Lock,
                            contentDescription = null,
                            tint = if (isAchieved) TideSuccess else TideTheme.colors.textTertiary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column {
                    Text(
                        text = milestone.title,
                        style = MaterialTheme.typography.titleSmall,
                        color = TideTheme.colors.textPrimary
                    )
                    Text(
                        text = milestone.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = TideTheme.colors.textSecondary
                    )
                }
            }

            if (isAchieved && dateAchievedStr != null) {
                Text(
                    text = dateAchievedStr,
                    style = MaterialTheme.typography.labelSmall,
                    color = TideSuccess
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddRecoveryGoalBottomSheet(
    onDismiss: () -> Unit,
    onSave: (name: String, amount: Double) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = TideTheme.colors.cardBackground,
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 16.dp)
        ) {
            Text(
                text = "New Savings Goal",
                style = MaterialTheme.typography.headlineMedium,
                color = TideTheme.colors.textPrimary
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "Celebrate your recovery with a goal you care about.",
                style = MaterialTheme.typography.bodyMedium,
                color = TideTheme.colors.textSecondary
            )

            Spacer(modifier = Modifier.height(20.dp))

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Goal Name") },
                placeholder = { Text("e.g. Vacation, New Device") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = TidePrimaryLuminous,
                    unfocusedBorderColor = TideTheme.colors.cardBorder,
                    focusedTextColor = TideTheme.colors.textPrimary,
                    unfocusedTextColor = TideTheme.colors.textPrimary
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = amount,
                onValueChange = { amount = it },
                label = { Text("Target Amount (₹)") },
                placeholder = { Text("25000") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = TidePrimaryLuminous,
                    unfocusedBorderColor = TideTheme.colors.cardBorder,
                    focusedTextColor = TideTheme.colors.textPrimary,
                    unfocusedTextColor = TideTheme.colors.textPrimary
                )
            )

            Spacer(modifier = Modifier.height(28.dp))

            TidePrimaryButton(
                text = "Create Goal",
                enabled = name.isNotBlank() && (amount.toDoubleOrNull() ?: 0.0) > 0,
                onClick = {
                    onSave(name.trim(), amount.toDoubleOrNull() ?: 0.0)
                    onDismiss()
                }
            )

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
