package com.tide.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "recovery_goals")
data class RecoveryGoalEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val targetAmount: Double,
    val currentAmount: Double = 0.0,
    val achieved: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)
