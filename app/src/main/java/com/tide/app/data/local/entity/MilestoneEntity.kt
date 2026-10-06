package com.tide.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "milestones")
data class MilestoneEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val type: String,
    val title: String,
    val description: String,
    val targetValue: Double,
    val achievedAt: Long? = null
)
