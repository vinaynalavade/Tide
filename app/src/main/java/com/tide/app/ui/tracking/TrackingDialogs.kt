package com.tide.app.ui.tracking

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material.icons.filled.Air
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Mood
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
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
import com.tide.app.data.local.entity.TriggerEntity
import kotlin.math.roundToInt

// QUICK LOG ACTION SHEET
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuickLogBottomSheet(
    onDismiss: () -> Unit,
    onSelectConsumption: () -> Unit,
    onSelectCraving: () -> Unit,
    onSelectSlip: () -> Unit,
    onSelectCheckIn: () -> Unit
) {
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
                text = "Log Event",
                style = MaterialTheme.typography.titleLarge,
                color = TideTheme.colors.textPrimary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Capture your journey accurately and without judgment.",
                style = MaterialTheme.typography.bodySmall,
                color = TideTheme.colors.textSecondary
            )

            Spacer(modifier = Modifier.height(24.dp))

            QuickActionTile(
                title = "Consumption",
                subtitle = "Log regular intake or usage",
                icon = Icons.Default.Add,
                accentColor = TidePrimaryLuminous,
                onClick = { onDismiss(); onSelectConsumption() }
            )

            Spacer(modifier = Modifier.height(12.dp))

            QuickActionTile(
                title = "Craving",
                subtitle = "Record an urge and whether you resisted",
                icon = Icons.Default.Air,
                accentColor = TideTheme.colors.warning,
                onClick = { onDismiss(); onSelectCraving() }
            )

            Spacer(modifier = Modifier.height(12.dp))

            QuickActionTile(
                title = "Slip",
                subtitle = "Unexpected use — information, not a verdict",
                icon = Icons.Outlined.Warning,
                accentColor = TideSlip,
                onClick = { onDismiss(); onSelectSlip() }
            )

            Spacer(modifier = Modifier.height(12.dp))

            QuickActionTile(
                title = "Daily Check-In",
                subtitle = "Mood, state, and reflections for today",
                icon = Icons.Default.Mood,
                accentColor = TideSuccess,
                onClick = { onDismiss(); onSelectCheckIn() }
            )

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
private fun QuickActionTile(
    title: String,
    subtitle: String,
    icon: ImageVector,
    accentColor: Color,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = TideTheme.colors.cardBackground,
        border = BorderStroke(1.dp, TideTheme.colors.cardBorder),
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = CircleShape,
                color = accentColor.copy(alpha = 0.15f),
                modifier = Modifier.size(44.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = accentColor,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    color = TideTheme.colors.textPrimary
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = TideTheme.colors.textSecondary
                )
            }
        }
    }
}

// CONSUMPTION LOG DIALOG
@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun LogConsumptionBottomSheet(
    unit: String,
    triggers: List<TriggerEntity>,
    onDismiss: () -> Unit,
    onSave: (amount: Double, triggerId: Long?, note: String?) -> Unit
) {
    var amount by remember { mutableFloatStateOf(1f) }
    var selectedTriggerId by remember { mutableStateOf<Long?>(null) }
    var note by remember { mutableStateOf("") }

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
                text = "Log Consumption",
                style = MaterialTheme.typography.headlineMedium,
                color = TideTheme.colors.textPrimary
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Amount Counter
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { if (amount > 1) amount -= 1f },
                    modifier = Modifier.size(48.dp)
                ) {
                    Icon(Icons.Default.Remove, contentDescription = "Decrement", tint = TideTheme.colors.textPrimary)
                }

                Spacer(modifier = Modifier.width(20.dp))

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "${amount.toInt()}",
                        style = MaterialTheme.typography.displayMedium,
                        fontWeight = FontWeight.Bold,
                        color = TidePrimaryLuminous
                    )
                    Text(
                        text = unit,
                        style = MaterialTheme.typography.labelMedium,
                        color = TideTheme.colors.textSecondary
                    )
                }

                Spacer(modifier = Modifier.width(20.dp))

                IconButton(
                    onClick = { amount += 1f },
                    modifier = Modifier.size(48.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Increment", tint = TideTheme.colors.textPrimary)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "What triggered this? (Optional)",
                style = MaterialTheme.typography.labelMedium,
                color = TideTheme.colors.textSecondary
            )

            Spacer(modifier = Modifier.height(10.dp))

            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                triggers.forEach { trigger ->
                    val isSelected = selectedTriggerId == trigger.id
                    SelectableChipMini(
                        label = trigger.name,
                        isSelected = isSelected,
                        onClick = {
                            selectedTriggerId = if (isSelected) null else trigger.id
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            OutlinedTextField(
                value = note,
                onValueChange = { note = it },
                label = { Text("Note (Optional)") },
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
                text = "Save Entry",
                onClick = {
                    onSave(amount.toDouble(), selectedTriggerId, note.ifBlank { null })
                    onDismiss()
                }
            )

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

// CRAVING LOG DIALOG
@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun LogCravingBottomSheet(
    triggers: List<TriggerEntity>,
    onDismiss: () -> Unit,
    onSave: (intensity: Int, triggerId: Long?, mood: String?, resisted: Boolean, note: String?) -> Unit
) {
    var intensity by remember { mutableIntStateOf(5) }
    var resisted by remember { mutableStateOf(true) }
    var selectedTriggerId by remember { mutableStateOf<Long?>(null) }
    var selectedMood by remember { mutableStateOf("Stressed") }
    var note by remember { mutableStateOf("") }

    val moods = listOf("Calm", "Anxious", "Stressed", "Restless", "Bored", "Sad")

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
                text = "Log Craving",
                style = MaterialTheme.typography.headlineMedium,
                color = TideTheme.colors.textPrimary
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Resisted Toggle
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = if (resisted) TideSuccess.copy(alpha = 0.15f) else TideTheme.colors.cardBackground,
                    border = BorderStroke(1.dp, if (resisted) TideSuccess else TideTheme.colors.cardBorder),
                    modifier = Modifier
                        .weight(1f)
                        .clickable { resisted = true }
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(Icons.Outlined.CheckCircle, contentDescription = null, tint = TideSuccess, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Resisted Wave", style = MaterialTheme.typography.labelMedium, color = TideTheme.colors.textPrimary)
                    }
                }

                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = if (!resisted) TideTheme.colors.warning.copy(alpha = 0.15f) else TideTheme.colors.cardBackground,
                    border = BorderStroke(1.dp, if (!resisted) TideTheme.colors.warning else TideTheme.colors.cardBorder),
                    modifier = Modifier
                        .weight(1f)
                        .clickable { resisted = false }
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text("Acted Upon", style = MaterialTheme.typography.labelMedium, color = TideTheme.colors.textPrimary)
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Intensity Slider
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Urge Intensity",
                    style = MaterialTheme.typography.labelMedium,
                    color = TideTheme.colors.textSecondary
                )
                Text(
                    text = "$intensity / 10",
                    style = MaterialTheme.typography.labelLarge,
                    color = TidePrimaryLuminous
                )
            }

            Slider(
                value = intensity.toFloat(),
                onValueChange = { intensity = it.roundToInt() },
                valueRange = 1f..10f,
                steps = 8,
                colors = SliderDefaults.colors(
                    thumbColor = TidePrimaryLuminous,
                    activeTrackColor = TidePrimary
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Mood
            Text(
                text = "Current Mood",
                style = MaterialTheme.typography.labelMedium,
                color = TideTheme.colors.textSecondary
            )
            Spacer(modifier = Modifier.height(8.dp))
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                moods.forEach { m ->
                    SelectableChipMini(
                        label = m,
                        isSelected = selectedMood == m,
                        onClick = { selectedMood = m }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Trigger
            Text(
                text = "Trigger (Optional)",
                style = MaterialTheme.typography.labelMedium,
                color = TideTheme.colors.textSecondary
            )
            Spacer(modifier = Modifier.height(8.dp))
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                triggers.forEach { trigger ->
                    val isSelected = selectedTriggerId == trigger.id
                    SelectableChipMini(
                        label = trigger.name,
                        isSelected = isSelected,
                        onClick = { selectedTriggerId = if (isSelected) null else trigger.id }
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            OutlinedTextField(
                value = note,
                onValueChange = { note = it },
                label = { Text("Note (Optional)") },
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
                text = "Save Craving",
                onClick = {
                    onSave(intensity, selectedTriggerId, selectedMood, resisted, note.ifBlank { null })
                    onDismiss()
                }
            )

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

// SLIP LOG DIALOG (Section 19: "A slip is NOT a failure state. You had a slip. Let's understand what happened.")
@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun LogSlipBottomSheet(
    unit: String,
    triggers: List<TriggerEntity>,
    onDismiss: () -> Unit,
    onSave: (amount: Double, triggerId: Long?, mood: String?, note: String?) -> Unit
) {
    var amount by remember { mutableFloatStateOf(1f) }
    var selectedTriggerId by remember { mutableStateOf<Long?>(null) }
    var selectedMood by remember { mutableStateOf("Overwhelmed") }
    var note by remember { mutableStateOf("") }

    val slipMoods = listOf("Overwhelmed", "Stressed", "Social Pressure", "Boredom", "Unexpected Urge")

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
                text = "You had a slip. Let's understand what happened.",
                style = MaterialTheme.typography.titleLarge,
                color = TideTheme.colors.textPrimary
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "This is information, not a verdict. Your total progress, savings, and clean history remain yours.",
                style = MaterialTheme.typography.bodySmall,
                color = TideTheme.colors.textSecondary
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Slip Amount
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { if (amount > 1) amount -= 1f }) {
                    Icon(Icons.Default.Remove, contentDescription = "Decrement", tint = TideTheme.colors.textPrimary)
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "${amount.toInt()}",
                        style = MaterialTheme.typography.displayMedium,
                        fontWeight = FontWeight.Bold,
                        color = TideSlip
                    )
                    Text(text = unit, style = MaterialTheme.typography.labelMedium, color = TideTheme.colors.textSecondary)
                }
                Spacer(modifier = Modifier.width(16.dp))
                IconButton(onClick = { amount += 1f }) {
                    Icon(Icons.Default.Add, contentDescription = "Increment", tint = TideTheme.colors.textPrimary)
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(text = "What was the context or trigger?", style = MaterialTheme.typography.labelMedium, color = TideTheme.colors.textSecondary)
            Spacer(modifier = Modifier.height(8.dp))
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                triggers.forEach { trigger ->
                    val isSelected = selectedTriggerId == trigger.id
                    SelectableChipMini(
                        label = trigger.name,
                        isSelected = isSelected,
                        onClick = { selectedTriggerId = if (isSelected) null else trigger.id }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(text = "How did you feel right before?", style = MaterialTheme.typography.labelMedium, color = TideTheme.colors.textSecondary)
            Spacer(modifier = Modifier.height(8.dp))
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                slipMoods.forEach { m ->
                    SelectableChipMini(
                        label = m,
                        isSelected = selectedMood == m,
                        onClick = { selectedMood = m }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = note,
                onValueChange = { note = it },
                label = { Text("What did you learn from this?") },
                placeholder = { Text("e.g. Next time I will step outside for air") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = TideSlip,
                    unfocusedBorderColor = TideTheme.colors.cardBorder,
                    focusedTextColor = TideTheme.colors.textPrimary,
                    unfocusedTextColor = TideTheme.colors.textPrimary
                )
            )

            Spacer(modifier = Modifier.height(28.dp))

            TidePrimaryButton(
                text = "Record & Continue Journey",
                onClick = {
                    onSave(amount.toDouble(), selectedTriggerId, selectedMood, note.ifBlank { null })
                    onDismiss()
                }
            )

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

// DAILY CHECK-IN DIALOG (Section 23: Lightweight daily check-in)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DailyCheckInBottomSheet(
    onDismiss: () -> Unit,
    onSave: (mood: String, cravingOccurred: Boolean, intensity: Int, note: String?) -> Unit
) {
    val moods = listOf("Great", "Good", "Okay", "Difficult", "Very difficult")
    var selectedMood by remember { mutableStateOf("Good") }
    var hadCravings by remember { mutableStateOf(false) }
    var cravingIntensity by remember { mutableIntStateOf(3) }
    var note by remember { mutableStateOf("") }

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
                text = "Daily Check-In",
                style = MaterialTheme.typography.headlineMedium,
                color = TideTheme.colors.textPrimary
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "How are you feeling today?",
                style = MaterialTheme.typography.bodyMedium,
                color = TideTheme.colors.textSecondary
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                moods.forEach { m ->
                    val isSelected = selectedMood == m
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (isSelected) TidePrimary.copy(alpha = 0.15f) else TideTheme.colors.cardBackground,
                        border = BorderStroke(1.dp, if (isSelected) TidePrimaryLuminous else TideTheme.colors.cardBorder),
                        modifier = Modifier
                            .weight(1f)
                            .clickable { selectedMood = m }
                    ) {
                        Text(
                            text = m,
                            fontSize = 11.sp,
                            style = MaterialTheme.typography.labelSmall,
                            color = if (isSelected) TidePrimaryLuminous else TideTheme.colors.textPrimary,
                            modifier = Modifier.padding(vertical = 12.dp),
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Any cravings today?",
                style = MaterialTheme.typography.titleMedium,
                color = TideTheme.colors.textPrimary
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                SelectableChipMini(
                    label = "No, felt peaceful",
                    isSelected = !hadCravings,
                    onClick = { hadCravings = false }
                )
                SelectableChipMini(
                    label = "Yes, had cravings",
                    isSelected = hadCravings,
                    onClick = { hadCravings = true }
                )
            }

            if (hadCravings) {
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("How strong?", style = MaterialTheme.typography.labelMedium, color = TideTheme.colors.textSecondary)
                    Text("$cravingIntensity / 10", style = MaterialTheme.typography.labelLarge, color = TidePrimaryLuminous)
                }
                Slider(
                    value = cravingIntensity.toFloat(),
                    onValueChange = { cravingIntensity = it.roundToInt() },
                    valueRange = 1f..10f,
                    steps = 8,
                    colors = SliderDefaults.colors(thumbColor = TidePrimaryLuminous, activeTrackColor = TidePrimary)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = note,
                onValueChange = { note = it },
                label = { Text("Reflections (Optional)") },
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
                text = "Complete Check-In",
                onClick = {
                    onSave(selectedMood, hadCravings, if (hadCravings) cravingIntensity else 0, note.ifBlank { null })
                    onDismiss()
                }
            )

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun SelectableChipMini(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = if (isSelected) TidePrimary.copy(alpha = 0.15f) else TideTheme.colors.cardBackground,
        border = BorderStroke(1.dp, if (isSelected) TidePrimaryLuminous else TideTheme.colors.cardBorder),
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (isSelected) {
                Icon(Icons.Default.Check, contentDescription = null, tint = TidePrimaryLuminous, modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(4.dp))
            }
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                color = if (isSelected) TidePrimaryLuminous else TideTheme.colors.textPrimary
            )
        }
    }
}
