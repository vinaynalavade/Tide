package com.tide.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_settings")
data class UserSettingsEntity(
    @PrimaryKey
    val id: Long = 1,
    val themeMode: String = "system", // "system", "light", "dark"
    val hapticsEnabled: Boolean = true,
    val soundEnabled: Boolean = true,
    val onboardingCompleted: Boolean = false
)
