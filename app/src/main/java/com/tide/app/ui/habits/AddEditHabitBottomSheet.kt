package com.tide.app.ui.habits

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.tide.app.core.theme.TidePrimary
import com.tide.app.core.theme.TidePrimaryLuminous
import com.tide.app.core.theme.TideSlip
import com.tide.app.core.theme.TideSuccess
import com.tide.app.core.theme.TideTheme
import com.tide.app.data.local.entity.AddictionEntity
import com.tide.app.data.local.entity.GoalEntity
import com.tide.app.data.local.entity.ProductEntity

data class HabitPreset(
    val name: String,
    val category: String,
    val unit: String,
    val goalType: String,
    val baselineAmount: Double,
    val targetAmount: Double,
    val hasFinancialImpact: Boolean,
    val defaultPrice: Double = 0.0,
    val unitCost: Double = 0.0
)

val HABIT_PRESETS = listOf(
    HabitPreset("Smoking", "Substance", "Cigarette", "QUIT", 10.0, 0.0, true, 200.0, 20.0),
    HabitPreset("Sugar", "Food & Nutrition", "Serving", "REDUCE", 4.0, 2.0, false),
    HabitPreset("Alcohol", "Substance", "Drink", "QUIT", 3.0, 0.0, true, 400.0, 200.0),
    HabitPreset("Caffeine", "Food & Nutrition", "Cup", "REDUCE", 4.0, 1.0, false),
    HabitPreset("Social Media", "Digital & Screen", "Minutes", "REDUCE", 120.0, 60.0, false),
    HabitPreset("Gaming", "Digital & Screen", "Hours", "TRACK", 3.0, 0.0, false),
    HabitPreset("Vaping", "Substance", "Session", "QUIT", 8.0, 0.0, true, 500.0, 50.0),
    HabitPreset("Gambling", "Behavioral", "Session", "QUIT", 2.0, 0.0, true, 1000.0, 1000.0),
    HabitPreset("Shopping", "Behavioral", "Purchase", "REDUCE", 3.0, 1.0, true, 500.0, 500.0),
    HabitPreset("Custom Habit", "Personal", "Time", "TRACK", 5.0, 2.0, false)
)

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun AddEditHabitBottomSheet(
    habitToEdit: AddictionEntity? = null,
    existingProduct: ProductEntity? = null,
    existingGoal: GoalEntity? = null,
    onDismiss: () -> Unit,
    onSaveHabit: (
        habit: AddictionEntity,
        product: ProductEntity?,
        goal: GoalEntity
    ) -> Unit
) {
    val isEditMode = habitToEdit != null

    var name by remember { mutableStateOf(habitToEdit?.name ?: "Sugar") }
    var category by remember { mutableStateOf(habitToEdit?.category ?: "Food & Nutrition") }
    var unit by remember { mutableStateOf(habitToEdit?.unit ?: "Serving") }
    var customUnit by remember { mutableStateOf(habitToEdit?.customUnit ?: "") }
    var goalType by remember { mutableStateOf(habitToEdit?.goalType ?: "REDUCE") }
    var baselineAmount by remember { mutableStateOf(habitToEdit?.baselineAmount?.toInt()?.toString() ?: "4") }
    var targetAmount by remember { mutableStateOf(existingGoal?.targetAmount?.toInt()?.toString() ?: "2") }
    var hasFinancialImpact by remember { mutableStateOf(habitToEdit?.hasFinancialImpact ?: false) }
    var price by remember { mutableStateOf(existingProduct?.price?.toInt()?.toString() ?: "100") }
    var brand by remember { mutableStateOf(existingProduct?.brand ?: "") }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = TideTheme.colors.cardBackground,
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp)
                .padding(top = 8.dp, bottom = 40.dp)
        ) {
            Text(
                text = if (isEditMode) "Edit Habit Configuration" else "Add New Habit",
                style = MaterialTheme.typography.titleLarge,
                color = TideTheme.colors.textPrimary
            )
            Text(
                text = if (isEditMode) "Current configuration is editable. Historical events remain immutable." else "Choose a preset or create a completely custom habit.",
                style = MaterialTheme.typography.bodySmall,
                color = TideTheme.colors.textSecondary
            )

            Spacer(modifier = Modifier.height(18.dp))

            // Presets row (only in add mode)
            if (!isEditMode) {
                Text("Popular Presets", style = MaterialTheme.typography.labelMedium, color = TideTheme.colors.textSecondary)
                Spacer(modifier = Modifier.height(8.dp))
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    HABIT_PRESETS.forEach { preset ->
                        val isSelected = name.equals(preset.name, ignoreCase = true)
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = if (isSelected) TidePrimary.copy(alpha = 0.2f) else TideTheme.colors.cardBorder.copy(alpha = 0.2f),
                            border = BorderStroke(1.dp, if (isSelected) TidePrimaryLuminous else Color.Transparent),
                            modifier = Modifier.clickable {
                                name = preset.name
                                category = preset.category
                                unit = preset.unit
                                goalType = preset.goalType
                                baselineAmount = preset.baselineAmount.toInt().toString()
                                targetAmount = preset.targetAmount.toInt().toString()
                                hasFinancialImpact = preset.hasFinancialImpact
                                if (preset.defaultPrice > 0) {
                                    price = preset.defaultPrice.toInt().toString()
                                }
                            }
                        ) {
                            Text(
                                text = preset.name,
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) TidePrimaryLuminous else TideTheme.colors.textPrimary,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(18.dp))
            }

            // Name field
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Habit Name") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = TidePrimaryLuminous,
                    unfocusedBorderColor = TideTheme.colors.cardBorder
                )
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Unit field
            OutlinedTextField(
                value = unit,
                onValueChange = { unit = it },
                label = { Text("Tracking Unit (e.g., serving, cigarette, cup, minutes)") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = TidePrimaryLuminous,
                    unfocusedBorderColor = TideTheme.colors.cardBorder
                )
            )

            Spacer(modifier = Modifier.height(18.dp))

            // Goal Selection (QUIT, REDUCE, TRACK)
            Text("Goal Mode", style = MaterialTheme.typography.labelMedium, color = TideTheme.colors.textSecondary)
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf("QUIT", "REDUCE", "TRACK").forEach { mode ->
                    val isSelected = goalType.equals(mode, ignoreCase = true)
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
                            .clickable { goalType = mode }
                    ) {
                        Column(
                            modifier = Modifier.padding(vertical = 10.dp),
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

            Spacer(modifier = Modifier.height(14.dp))

            // Baseline Amount
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = baselineAmount,
                    onValueChange = { baselineAmount = it },
                    label = { Text("Daily Baseline ($unit)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.weight(1f),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = TidePrimaryLuminous,
                        unfocusedBorderColor = TideTheme.colors.cardBorder
                    )
                )

                if (goalType.equals("REDUCE", ignoreCase = true)) {
                    OutlinedTextField(
                        value = targetAmount,
                        onValueChange = { targetAmount = it },
                        label = { Text("Daily Target ($unit)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.weight(1f),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = TideSuccess,
                            unfocusedBorderColor = TideTheme.colors.cardBorder
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Financial Impact Section
            TideCard {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Track Financial Cost", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold, color = TideTheme.colors.textPrimary)
                            Text("Calculate money spent and avoided. (Disable for habits without financial cost)", style = MaterialTheme.typography.bodySmall, color = TideTheme.colors.textSecondary)
                        }
                        Switch(
                            checked = hasFinancialImpact,
                            onCheckedChange = { hasFinancialImpact = it },
                            colors = SwitchDefaults.colors(checkedThumbColor = TidePrimaryLuminous, checkedTrackColor = TidePrimary)
                        )
                    }

                    if (hasFinancialImpact) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            OutlinedTextField(
                                value = price,
                                onValueChange = { price = it },
                                label = { Text("Cost per $unit (₹)") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                singleLine = true,
                                modifier = Modifier.weight(1f),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = TidePrimaryLuminous,
                                    unfocusedBorderColor = TideTheme.colors.cardBorder
                                )
                            )
                            OutlinedTextField(
                                value = brand,
                                onValueChange = { brand = it },
                                label = { Text("Brand (Optional)") },
                                singleLine = true,
                                modifier = Modifier.weight(1f),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = TidePrimaryLuminous,
                                    unfocusedBorderColor = TideTheme.colors.cardBorder
                                )
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Save button
            TidePrimaryButton(
                text = if (isEditMode) "Save Changes" else "Create Habit",
                onClick = {
                    val baseAmt = baselineAmount.toDoubleOrNull() ?: 1.0
                    val tgtAmt = targetAmount.toDoubleOrNull() ?: 0.0
                    val priceAmt = price.toDoubleOrNull() ?: 0.0
                    val unitCost = if (hasFinancialImpact) priceAmt else 0.0
                    val baselineDailyCost = if (hasFinancialImpact) baseAmt * unitCost else 0.0

                    val habit = AddictionEntity(
                        id = habitToEdit?.id ?: 0L,
                        name = name.trim().ifBlank { "Habit" },
                        category = category.trim().ifBlank { "Personal" },
                        unit = unit.trim().ifBlank { "unit" },
                        customUnit = customUnit.trim().ifBlank { null },
                        baselineAmount = baseAmt,
                        baselineFrequency = "day",
                        baselineCost = baselineDailyCost,
                        currency = "INR",
                        goalType = goalType.uppercase(),
                        startDate = habitToEdit?.startDate ?: System.currentTimeMillis(),
                        active = true,
                        hasFinancialImpact = hasFinancialImpact,
                        hasTimeImpact = false,
                        unitType = "COUNT",
                        isArchived = false,
                        displayOrder = habitToEdit?.displayOrder ?: 0
                    )

                    val product = if (hasFinancialImpact) {
                        ProductEntity(
                            id = existingProduct?.id ?: 0L,
                            addictionId = habit.id,
                            brand = brand.trim().ifBlank { "Default" },
                            productName = name.trim(),
                            unit = unit.trim(),
                            price = priceAmt,
                            pricingBasis = "per unit",
                            unitsPerBasis = 1.0,
                            unitCost = unitCost
                        )
                    } else null

                    val goal = GoalEntity(
                        id = existingGoal?.id ?: 0L,
                        addictionId = habit.id,
                        type = goalType.uppercase(),
                        targetAmount = if (goalType == "REDUCE") tgtAmt else 0.0,
                        targetFrequency = "day",
                        active = true
                    )

                    onSaveHabit(habit, product, goal)
                    onDismiss()
                }
            )
        }
    }
}
