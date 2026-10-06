package com.tide.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "addictions")
data class AddictionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val category: String,
    val unit: String,
    val customUnit: String? = null,
    val baselineAmount: Double,
    val baselineFrequency: String = "day", // "day", "week", "month"
    val baselineCost: Double, // Cost per day baseline
    val currency: String = "INR",
    val goalType: String, // "QUIT", "REDUCE", "TRACK"
    val startDate: Long, // Epoch millis
    val active: Boolean = true
)
