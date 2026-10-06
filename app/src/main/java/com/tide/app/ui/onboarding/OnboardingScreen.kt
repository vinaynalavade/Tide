package com.tide.app.ui.onboarding

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tide.app.core.designsystem.TideCard
import com.tide.app.core.designsystem.TidePrimaryButton
import com.tide.app.core.designsystem.TideSecondaryButton
import com.tide.app.core.theme.TidePrimary
import com.tide.app.core.theme.TidePrimaryLuminous
import com.tide.app.core.theme.TideTheme

@Composable
fun OnboardingScreen(
    viewModel: OnboardingViewModel,
    onOnboardingFinished: () -> Unit
) {
    val state by viewModel.state.collectAsState()

    if (state.isComplete) {
        onOnboardingFinished()
        return
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp, vertical = 20.dp)
    ) {
        AnimatedContent(
            targetState = state.step,
            transitionSpec = { fadeIn() togetherWith fadeOut() },
            label = "onboarding_steps"
        ) { step ->
            when (step) {
                OnboardingStep.WELCOME -> {
                    WelcomeStep(onBegin = { viewModel.updateStep(OnboardingStep.ADDICTION_SELECT) })
                }
                OnboardingStep.ADDICTION_SELECT -> {
                    AddictionSelectStep(
                        selected = state.selectedCategory,
                        customName = state.customAddictionName,
                        onSelect = { viewModel.selectCategory(it) },
                        onCustomNameChange = { viewModel.updateCustomAddictionName(it) },
                        onNext = { viewModel.updateStep(OnboardingStep.PRODUCT_CONFIG) },
                        onBack = { viewModel.updateStep(OnboardingStep.WELCOME) }
                    )
                }
                OnboardingStep.PRODUCT_CONFIG -> {
                    ProductConfigStep(
                        category = state.selectedCategory,
                        brand = state.brandName,
                        product = state.productName,
                        unit = state.selectedUnit,
                        customUnit = state.customUnit,
                        onUpdate = { b, p, u, cu -> viewModel.updateProductInfo(b, p, u, cu) },
                        onNext = { viewModel.updateStep(OnboardingStep.PRICE_CONFIG) },
                        onBack = { viewModel.updateStep(OnboardingStep.ADDICTION_SELECT) }
                    )
                }
                OnboardingStep.PRICE_CONFIG -> {
                    PriceConfigStep(
                        unit = state.selectedUnit,
                        price = state.priceAmount,
                        basis = state.pricingBasis,
                        unitsPerBasis = state.unitsPerBasis,
                        onUpdate = { p, b, upb -> viewModel.updatePrice(p, b, upb) },
                        onNext = { viewModel.updateStep(OnboardingStep.BASELINE_CONFIG) },
                        onBack = { viewModel.updateStep(OnboardingStep.PRODUCT_CONFIG) }
                    )
                }
                OnboardingStep.BASELINE_CONFIG -> {
                    BaselineConfigStep(
                        unit = state.selectedUnit,
                        amount = state.baselineAmount,
                        frequency = state.baselineFrequency,
                        onUpdate = { a, f -> viewModel.updateBaseline(a, f) },
                        onNext = { viewModel.updateStep(OnboardingStep.GOAL_SELECT) },
                        onBack = { viewModel.updateStep(OnboardingStep.PRICE_CONFIG) }
                    )
                }
                OnboardingStep.GOAL_SELECT -> {
                    GoalSelectStep(
                        selectedGoal = state.goalType,
                        onSelect = { viewModel.selectGoalType(it) },
                        onNext = { viewModel.updateStep(OnboardingStep.GOAL_SETUP) },
                        onBack = { viewModel.updateStep(OnboardingStep.BASELINE_CONFIG) }
                    )
                }
                OnboardingStep.GOAL_SETUP -> {
                    GoalSetupStep(
                        goalType = state.goalType,
                        baselineAmount = state.baselineAmount,
                        unit = state.selectedUnit,
                        quitChoice = state.quitDateChoice,
                        reduceTarget = state.reduceTargetAmount,
                        isLoading = state.isLoading,
                        onUpdate = { qc, rt -> viewModel.updateGoalSetup(qc, rt) },
                        onFinish = { viewModel.completeOnboarding() },
                        onBack = { viewModel.updateStep(OnboardingStep.GOAL_SELECT) }
                    )
                }
            }
        }
    }
}

@Composable
private fun WelcomeStep(onBegin: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Spacer(modifier = Modifier.height(60.dp))

        Column {
            Text(
                text = "Tide",
                style = MaterialTheme.typography.displayLarge.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = (-1).sp
                ),
                color = TideTheme.colors.textPrimary
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Understand the pattern.\nChange the tide.",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Light,
                    lineHeight = 34.sp
                ),
                color = TideTheme.colors.textSecondary
            )
        }

        Column {
            TidePrimaryButton(
                text = "Begin",
                onClick = onBegin
            )
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun AddictionSelectStep(
    selected: String,
    customName: String,
    onSelect: (String) -> Unit,
    onCustomNameChange: (String) -> Unit,
    onNext: () -> Unit,
    onBack: () -> Unit
) {
    val categories = listOf(
        "Smoking", "Alcohol", "Vaping", "Cannabis",
        "Gambling", "Social Media", "Gaming",
        "Food / Sugar", "Caffeine", "Shopping", "Other"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            StepHeader(
                title = "What are you trying to change?",
                subtitle = "Tide adapts to any habit or compulsion.",
                onBack = onBack
            )

            Spacer(modifier = Modifier.height(24.dp))

            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                categories.forEach { category ->
                    val isSelected = selected == category
                    SelectableChip(
                        label = category,
                        isSelected = isSelected,
                        onClick = { onSelect(category) }
                    )
                }
            }

            if (selected == "Other") {
                Spacer(modifier = Modifier.height(20.dp))
                TideInputField(
                    value = customName,
                    onValueChange = onCustomNameChange,
                    label = "Addiction name",
                    placeholder = "e.g. Energy Drinks, Nails biting"
                )
            }
        }

        Column {
            Spacer(modifier = Modifier.height(32.dp))
            TidePrimaryButton(
                text = "Continue",
                onClick = onNext,
                enabled = selected != "Other" || customName.isNotBlank()
            )
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun ProductConfigStep(
    category: String,
    brand: String,
    product: String,
    unit: String,
    customUnit: String,
    onUpdate: (String, String, String, String) -> Unit,
    onNext: () -> Unit,
    onBack: () -> Unit
) {
    val supportedUnits = listOf(
        "Piece", "Cigarette", "Bottle", "Can", "Glass",
        "ml", "Gram", "Session", "Hour", "Day", "₹ amount", "Custom"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            StepHeader(
                title = "Product & Brand",
                subtitle = "Configure what you consume or do.",
                onBack = onBack
            )

            Spacer(modifier = Modifier.height(24.dp))

            TideInputField(
                value = brand,
                onValueChange = { onUpdate(it, product, unit, customUnit) },
                label = "Brand / Source (Optional)",
                placeholder = "e.g. Marlboro, Kingfisher"
            )

            Spacer(modifier = Modifier.height(16.dp))

            TideInputField(
                value = product,
                onValueChange = { onUpdate(brand, it, unit, customUnit) },
                label = "Product / Item (Optional)",
                placeholder = "e.g. Lights, Beer, Screen Time"
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Tracking Unit",
                style = MaterialTheme.typography.labelMedium,
                color = TideTheme.colors.textSecondary
            )

            Spacer(modifier = Modifier.height(10.dp))

            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                supportedUnits.forEach { u ->
                    SelectableChip(
                        label = u,
                        isSelected = unit == u,
                        onClick = { onUpdate(brand, product, u, customUnit) }
                    )
                }
            }

            if (unit == "Custom") {
                Spacer(modifier = Modifier.height(16.dp))
                TideInputField(
                    value = customUnit,
                    onValueChange = { onUpdate(brand, product, unit, it) },
                    label = "Custom Unit",
                    placeholder = "e.g. vape, round"
                )
            }
        }

        Column {
            Spacer(modifier = Modifier.height(32.dp))
            TidePrimaryButton(
                text = "Continue",
                onClick = onNext
            )
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun PriceConfigStep(
    unit: String,
    price: String,
    basis: String,
    unitsPerBasis: String,
    onUpdate: (String, String, String) -> Unit,
    onNext: () -> Unit,
    onBack: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            StepHeader(
                title = "How much does it usually cost?",
                subtitle = "Used to calculate your real money saved.",
                onBack = onBack
            )

            Spacer(modifier = Modifier.height(24.dp))

            TideInputField(
                value = price,
                onValueChange = { onUpdate(it, basis, unitsPerBasis) },
                label = "Price (₹)",
                placeholder = "200",
                keyboardType = KeyboardType.Number
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Pricing Basis",
                style = MaterialTheme.typography.labelMedium,
                color = TideTheme.colors.textSecondary
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf("per unit", "per pack", "per session", "per day").forEach { b ->
                    SelectableChip(
                        label = b,
                        isSelected = basis == b,
                        onClick = { onUpdate(price, b, unitsPerBasis) }
                    )
                }
            }

            if (basis == "per pack") {
                Spacer(modifier = Modifier.height(16.dp))
                TideInputField(
                    value = unitsPerBasis,
                    onValueChange = { onUpdate(price, basis, it) },
                    label = "Items in one pack",
                    placeholder = "10 or 20",
                    keyboardType = KeyboardType.Number
                )
            }
        }

        Column {
            Spacer(modifier = Modifier.height(32.dp))
            TidePrimaryButton(
                text = "Continue",
                onClick = onNext
            )
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun BaselineConfigStep(
    unit: String,
    amount: String,
    frequency: String,
    onUpdate: (String, String) -> Unit,
    onNext: () -> Unit,
    onBack: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            StepHeader(
                title = "How much do you usually consume?",
                subtitle = "Your baseline reference to measure real progress.",
                onBack = onBack
            )

            Spacer(modifier = Modifier.height(32.dp))

            TideInputField(
                value = amount,
                onValueChange = { onUpdate(it, frequency) },
                label = "Typical amount ($unit)",
                placeholder = "8",
                keyboardType = KeyboardType.Number
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                listOf("day", "week").forEach { freq ->
                    SelectableChip(
                        label = "per $freq",
                        isSelected = frequency == freq,
                        onClick = { onUpdate(amount, freq) }
                    )
                }
            }
        }

        Column {
            Spacer(modifier = Modifier.height(32.dp))
            TidePrimaryButton(
                text = "Continue",
                onClick = onNext
            )
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun GoalSelectStep(
    selectedGoal: String,
    onSelect: (String) -> Unit,
    onNext: () -> Unit,
    onBack: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            StepHeader(
                title = "What do you want to do?",
                subtitle = "Choose your journey. No pressure.",
                onBack = onBack
            )

            Spacer(modifier = Modifier.height(28.dp))

            GoalOptionCard(
                title = "QUIT",
                description = "Stop completely. Track your clean days and recovered life.",
                isSelected = selectedGoal == "QUIT",
                onClick = { onSelect("QUIT") }
            )

            Spacer(modifier = Modifier.height(14.dp))

            GoalOptionCard(
                title = "REDUCE",
                description = "Gradually reduce consumption with healthy daily targets.",
                isSelected = selectedGoal == "REDUCE",
                onClick = { onSelect("REDUCE") }
            )

            Spacer(modifier = Modifier.height(14.dp))

            GoalOptionCard(
                title = "TRACK",
                description = "Observe and understand your patterns before deciding.",
                isSelected = selectedGoal == "TRACK",
                onClick = { onSelect("TRACK") }
            )
        }

        Column {
            Spacer(modifier = Modifier.height(32.dp))
            TidePrimaryButton(
                text = "Continue",
                onClick = onNext
            )
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun GoalSetupStep(
    goalType: String,
    baselineAmount: String,
    unit: String,
    quitChoice: String,
    reduceTarget: String,
    isLoading: Boolean,
    onUpdate: (String, String) -> Unit,
    onFinish: () -> Unit,
    onBack: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            StepHeader(
                title = when (goalType) {
                    "QUIT" -> "When did you stop?"
                    "REDUCE" -> "Set your daily target"
                    else -> "Ready to begin"
                },
                subtitle = when (goalType) {
                    "QUIT" -> "We will start your streak from this moment."
                    "REDUCE" -> "Current baseline is $baselineAmount $unit per day."
                    else -> "Track without pressure and watch your patterns emerge."
                },
                onBack = onBack
            )

            Spacer(modifier = Modifier.height(28.dp))

            when (goalType) {
                "QUIT" -> {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        listOf("Today", "Yesterday").forEach { choice ->
                            SelectableChip(
                                label = choice,
                                isSelected = quitChoice == choice,
                                onClick = { onUpdate(choice, reduceTarget) }
                            )
                        }
                    }
                }
                "REDUCE" -> {
                    TideInputField(
                        value = reduceTarget,
                        onValueChange = { onUpdate(quitChoice, it) },
                        label = "Target per day ($unit)",
                        placeholder = "4",
                        keyboardType = KeyboardType.Number
                    )
                }
                else -> {
                    TideCard {
                        Text(
                            text = "Every log is simply information to help you understand your triggers and habits.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TideTheme.colors.textSecondary
                        )
                    }
                }
            }
        }

        Column {
            Spacer(modifier = Modifier.height(32.dp))
            if (isLoading) {
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = TidePrimary)
                }
            } else {
                TidePrimaryButton(
                    text = "Set Sail with Tide",
                    onClick = onFinish
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun StepHeader(
    title: String,
    subtitle: String,
    onBack: () -> Unit
) {
    Column {
        IconButton(
            onClick = onBack,
            modifier = Modifier.size(36.dp)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                tint = TideTheme.colors.textPrimary
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.headlineLarge,
            color = TideTheme.colors.textPrimary
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = subtitle,
            style = MaterialTheme.typography.bodyMedium,
            color = TideTheme.colors.textSecondary
        )
    }
}

@Composable
private fun SelectableChip(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = if (isSelected) TidePrimary.copy(alpha = 0.15f) else TideTheme.colors.cardBackground,
        border = BorderStroke(
            1.dp,
            if (isSelected) TidePrimaryLuminous else TideTheme.colors.cardBorder
        ),
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (isSelected) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = TidePrimaryLuminous
                )
                Spacer(modifier = Modifier.width(6.dp))
            }
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = if (isSelected) TidePrimaryLuminous else TideTheme.colors.textPrimary
            )
        }
    }
}

@Composable
private fun GoalOptionCard(
    title: String,
    description: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    TideCard(
        borderColor = if (isSelected) TidePrimaryLuminous else TideTheme.colors.cardBorder,
        backgroundColor = if (isSelected) TidePrimary.copy(alpha = 0.08f) else TideTheme.colors.cardBackground,
        onClick = onClick
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (isSelected) TidePrimaryLuminous else TideTheme.colors.textPrimary
                )
                if (isSelected) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        tint = TidePrimaryLuminous,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = TideTheme.colors.textSecondary
            )
        }
    }
}

@Composable
private fun TideInputField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String = "",
    keyboardType: KeyboardType = KeyboardType.Text
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(text = label) },
        placeholder = { Text(text = placeholder) },
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = TidePrimaryLuminous,
            unfocusedBorderColor = TideTheme.colors.cardBorder,
            focusedTextColor = TideTheme.colors.textPrimary,
            unfocusedTextColor = TideTheme.colors.textPrimary
        )
    )
}
