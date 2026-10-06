package com.tide.app.ui.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tide.app.data.local.entity.AddictionEntity
import com.tide.app.data.local.entity.GoalEntity
import com.tide.app.data.local.entity.ProductEntity
import com.tide.app.data.local.entity.UserSettingsEntity
import com.tide.app.data.repository.TideRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.ZoneId

enum class OnboardingStep {
    WELCOME,
    ADDICTION_SELECT,
    PRODUCT_CONFIG,
    PRICE_CONFIG,
    BASELINE_CONFIG,
    GOAL_SELECT,
    GOAL_SETUP
}

data class OnboardingState(
    val step: OnboardingStep = OnboardingStep.WELCOME,
    // Addiction
    val selectedCategory: String = "Smoking",
    val customAddictionName: String = "",
    // Product
    val brandName: String = "",
    val productName: String = "",
    val selectedUnit: String = "Cigarette",
    val customUnit: String = "",
    // Price
    val priceAmount: String = "200",
    val currency: String = "INR",
    val pricingBasis: String = "per pack", // "per pack", "per unit", "per session", "per day"
    val unitsPerBasis: String = "10",
    // Baseline
    val baselineAmount: String = "8",
    val baselineFrequency: String = "day",
    // Goal
    val goalType: String = "QUIT", // "QUIT", "REDUCE", "TRACK"
    val quitDateChoice: String = "Today", // "Today", "Yesterday"
    val reduceTargetAmount: String = "4",
    val isLoading: Boolean = false,
    val isComplete: Boolean = false
)

class OnboardingViewModel(
    private val repository: TideRepository
) : ViewModel() {

    private val _state = MutableStateFlow(OnboardingState())
    val state: StateFlow<OnboardingState> = _state.asStateFlow()

    fun updateStep(step: OnboardingStep) {
        _state.value = _state.value.copy(step = step)
    }

    fun selectCategory(category: String) {
        val defaultUnit = when (category) {
            "Smoking" -> "Cigarette"
            "Alcohol" -> "Bottle"
            "Vaping" -> "Session"
            "Cannabis" -> "Gram"
            "Gambling" -> "₹ amount"
            "Social Media" -> "Hour"
            "Gaming" -> "Hour"
            "Food / Sugar" -> "Piece"
            "Caffeine" -> "Cup"
            "Shopping" -> "₹ amount"
            else -> "Piece"
        }
        val defaultBasis = when (category) {
            "Smoking" -> "per pack"
            "Alcohol" -> "per bottle"
            "Vaping" -> "per pod"
            else -> "per unit"
        }
        val defaultUnitsPerBasis = when (category) {
            "Smoking" -> "10"
            else -> "1"
        }

        _state.value = _state.value.copy(
            selectedCategory = category,
            selectedUnit = defaultUnit,
            pricingBasis = defaultBasis,
            unitsPerBasis = defaultUnitsPerBasis
        )
    }

    fun updateCustomAddictionName(name: String) {
        _state.value = _state.value.copy(customAddictionName = name)
    }

    fun updateProductInfo(brand: String, product: String, unit: String, customUnit: String) {
        _state.value = _state.value.copy(
            brandName = brand,
            productName = product,
            selectedUnit = unit,
            customUnit = customUnit
        )
    }

    fun updatePrice(price: String, basis: String, unitsPerBasis: String) {
        _state.value = _state.value.copy(
            priceAmount = price,
            pricingBasis = basis,
            unitsPerBasis = unitsPerBasis
        )
    }

    fun updateBaseline(amount: String, frequency: String) {
        _state.value = _state.value.copy(
            baselineAmount = amount,
            baselineFrequency = frequency
        )
    }

    fun selectGoalType(type: String) {
        _state.value = _state.value.copy(goalType = type)
    }

    fun updateGoalSetup(quitChoice: String, reduceTarget: String) {
        _state.value = _state.value.copy(
            quitDateChoice = quitChoice,
            reduceTargetAmount = reduceTarget
        )
    }

    fun completeOnboarding() {
        val s = _state.value
        _state.value = s.copy(isLoading = true)

        viewModelScope.launch {
            val addictionName = if (s.selectedCategory == "Other" && s.customAddictionName.isNotBlank()) {
                s.customAddictionName.trim()
            } else {
                s.selectedCategory
            }

            val finalUnit = if (s.selectedUnit == "Custom" && s.customUnit.isNotBlank()) {
                s.customUnit.trim()
            } else {
                s.selectedUnit
            }

            val priceValue = s.priceAmount.toDoubleOrNull() ?: 200.0
            val unitsInBasis = s.unitsPerBasis.toDoubleOrNull() ?: 1.0
            val unitCost = if (unitsInBasis > 0) priceValue / unitsInBasis else priceValue

            val baselineAmountVal = s.baselineAmount.toDoubleOrNull() ?: 8.0
            val baselineDailyCost = baselineAmountVal * unitCost

            val startDateMillis = when (s.quitDateChoice) {
                "Yesterday" -> LocalDate.now().minusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
                else -> LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
            }

            val addictionEntity = AddictionEntity(
                name = addictionName,
                category = s.selectedCategory,
                unit = finalUnit,
                customUnit = if (s.selectedUnit == "Custom") s.customUnit else null,
                baselineAmount = baselineAmountVal,
                baselineFrequency = s.baselineFrequency,
                baselineCost = baselineDailyCost,
                currency = s.currency,
                goalType = s.goalType,
                startDate = startDateMillis,
                active = true
            )

            val productEntity = ProductEntity(
                addictionId = 0L,
                brand = if (s.brandName.isNotBlank()) s.brandName.trim() else "Default",
                productName = if (s.productName.isNotBlank()) s.productName.trim() else addictionName,
                unit = finalUnit,
                price = priceValue,
                pricingBasis = s.pricingBasis,
                unitsPerBasis = unitsInBasis,
                unitCost = unitCost
            )

            val targetAmt = if (s.goalType == "REDUCE") {
                s.reduceTargetAmount.toDoubleOrNull() ?: (baselineAmountVal / 2)
            } else 0.0

            val goalEntity = GoalEntity(
                addictionId = 0L,
                type = s.goalType,
                targetAmount = targetAmt,
                targetFrequency = "day",
                active = true
            )

            val defaultTriggers = when (s.selectedCategory) {
                "Smoking", "Vaping" -> listOf("Stress", "Social / Friends", "After Meals", "Boredom", "Driving", "Morning Routine")
                "Alcohol" -> listOf("Social Gathering", "Stress", "Celebration", "Evening Wind-down", "Boredom", "Sadness")
                "Social Media", "Gaming" -> listOf("Boredom", "Procrastination", "Late Night", "Stress", "Loneliness")
                "Gambling", "Shopping" -> listOf("Stress", "Impulse", "Boredom", "Financial Worry", "Social Pressure")
                "Food / Sugar", "Caffeine" -> listOf("Stress", "Fatigue", "Craving Taste", "Boredom", "Habit")
                else -> listOf("Stress", "Boredom", "Social", "Habitual Routine", "Emotional Trigger")
            }

            // Atomically persist all onboarding entities and complete status
            repository.completeOnboardingTransaction(
                addiction = addictionEntity,
                product = productEntity,
                goal = goalEntity,
                defaultTriggers = defaultTriggers
            )

            _state.value = _state.value.copy(isLoading = false, isComplete = true)
        }
    }
}
