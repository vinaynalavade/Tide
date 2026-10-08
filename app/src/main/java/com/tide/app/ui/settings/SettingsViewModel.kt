package com.tide.app.ui.settings

import android.app.Activity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tide.app.core.update.InAppUpdateManager
import com.tide.app.core.update.UpdateState
import com.tide.app.data.local.entity.AddictionEntity
import com.tide.app.data.local.entity.GoalEntity
import com.tide.app.data.local.entity.NotificationPreferenceEntity
import com.tide.app.data.local.entity.ProductEntity
import com.tide.app.data.local.entity.UserSettingsEntity
import com.tide.app.data.repository.TideRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

data class SettingsUiState(
    val isLoading: Boolean = true,
    val activeHabits: List<AddictionEntity> = emptyList(),
    val archivedHabits: List<AddictionEntity> = emptyList(),
    val product: ProductEntity? = null,
    val userSettings: UserSettingsEntity = UserSettingsEntity(),
    val notificationPreferences: List<NotificationPreferenceEntity> = emptyList(),
    val exportedJson: String? = null,
    val isDataDeleted: Boolean = false
) {
    val addiction: AddictionEntity? get() = activeHabits.firstOrNull()
}

class SettingsViewModel(
    private val repository: TideRepository,
    val inAppUpdateManager: InAppUpdateManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    val updateState: StateFlow<UpdateState> = inAppUpdateManager.updateState

    init {
        loadSettings()
    }

    private fun loadSettings() {
        viewModelScope.launch {
            combine(
                repository.getActiveHabits(),
                repository.getArchivedHabits(),
                repository.getUserSettings(),
                repository.getNotificationPreferences()
            ) { active, archived, settings, prefs ->
                SettingsUiState(
                    isLoading = false,
                    activeHabits = active,
                    archivedHabits = archived,
                    product = null,
                    userSettings = settings ?: UserSettingsEntity(),
                    notificationPreferences = prefs
                )
            }.collect { state ->
                _uiState.value = state
            }
        }
    }

    fun archiveHabit(habitId: Long) {
        viewModelScope.launch {
            repository.archiveHabit(habitId)
        }
    }

    fun restoreHabit(habitId: Long) {
        viewModelScope.launch {
            repository.restoreHabit(habitId)
        }
    }

    fun deleteHabit(habitId: Long) {
        viewModelScope.launch {
            repository.deleteHabit(habitId)
        }
    }

    fun createHabit(habit: AddictionEntity, product: ProductEntity?, goal: GoalEntity) {
        viewModelScope.launch {
            val defaultTriggers = when (habit.category) {
                "Substance" -> listOf("Stress", "Social situation", "After food", "Boredom", "Routine")
                "Food & Nutrition" -> listOf("Stress", "Cravings", "Boredom", "Fatigue", "Social")
                "Digital & Screen" -> listOf("Boredom", "Procrastination", "Late night", "Stress")
                "Behavioral" -> listOf("Impulse", "Stress", "Boredom", "Social")
                else -> listOf("Stress", "Boredom", "Habit", "Social")
            }
            repository.createHabit(habit, product, goal, defaultTriggers)
        }
    }

    fun updateHabitConfig(habit: AddictionEntity, product: ProductEntity?, goal: GoalEntity?) {
        viewModelScope.launch {
            repository.updateHabitConfig(habit, product, goal)
        }
    }

    fun setTheme(themeMode: String) {
        val current = _uiState.value.userSettings
        viewModelScope.launch {
            repository.updateUserSettings(current.copy(themeMode = themeMode))
        }
    }

    fun toggleHaptics(enabled: Boolean) {
        val current = _uiState.value.userSettings
        viewModelScope.launch {
            repository.updateUserSettings(current.copy(hapticsEnabled = enabled))
        }
    }

    fun toggleNotificationPreference(pref: NotificationPreferenceEntity) {
        viewModelScope.launch {
            repository.updateNotificationPreference(pref.copy(enabled = !pref.enabled))
        }
    }

    fun exportData() {
        viewModelScope.launch {
            val json = repository.exportAllDataJson()
            _uiState.value = _uiState.value.copy(exportedJson = json)
        }
    }

    fun clearExportDialog() {
        _uiState.value = _uiState.value.copy(exportedJson = null)
    }

    fun deleteAllData() {
        viewModelScope.launch {
            repository.deleteAllData()
            _uiState.value = _uiState.value.copy(isDataDeleted = true)
        }
    }

    fun checkForUpdates() {
        inAppUpdateManager.checkForUpdates()
    }

    fun startUpdate(activity: Activity) {
        inAppUpdateManager.startUpdate(activity)
    }

    fun completeUpdate() {
        inAppUpdateManager.completeUpdate()
    }
}
