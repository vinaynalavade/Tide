package com.tide.app.ui.craving

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tide.app.data.local.entity.CravingEntryEntity
import com.tide.app.data.repository.TideRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

enum class CravingStage {
    BREATHING,
    CHECK_IN,
    COMPLETED_BETTER,
    SUPPORT_OPTIONS
}

data class CravingModeState(
    val stage: CravingStage = CravingStage.BREATHING,
    val selectedResult: String? = null, // "Better", "Same", "Worse"
    val isSaved: Boolean = false
)

class CravingModeViewModel(
    private val repository: TideRepository
) : ViewModel() {

    private val _state = MutableStateFlow(CravingModeState())
    val state: StateFlow<CravingModeState> = _state.asStateFlow()

    fun onBreathingCompleted() {
        _state.value = _state.value.copy(stage = CravingStage.CHECK_IN)
    }

    fun submitFeedback(result: String) {
        _state.value = _state.value.copy(selectedResult = result)

        val resisted = result == "Better" || result == "Same"
        viewModelScope.launch {
            val addiction = repository.getActiveAddictionSync()
            if (addiction != null) {
                repository.logCraving(
                    CravingEntryEntity(
                        addictionId = addiction.id,
                        timestamp = System.currentTimeMillis(),
                        intensity = if (result == "Better") 3 else 7,
                        mood = if (result == "Better") "Calm" else "Struggling",
                        resisted = resisted,
                        note = "Rode the wave via Craving Mode ($result)"
                    )
                )
            }

            if (result == "Better") {
                _state.value = _state.value.copy(stage = CravingStage.COMPLETED_BETTER, isSaved = true)
            } else {
                _state.value = _state.value.copy(stage = CravingStage.SUPPORT_OPTIONS, isSaved = true)
            }
        }
    }

    fun restartBreathing() {
        _state.value = CravingModeState(stage = CravingStage.BREATHING)
    }
}
