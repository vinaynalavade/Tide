package com.tide.app.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "daily_check_ins",
    foreignKeys = [
        ForeignKey(
            entity = AddictionEntity::class,
            parentColumns = ["id"],
            childColumns = ["addictionId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index("addictionId"),
        Index(value = ["addictionId", "date"], unique = true)
    ]
)
data class DailyCheckInEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val addictionId: Long,
    val date: String, // "YYYY-MM-DD"
    val mood: String, // "Great", "Good", "Okay", "Difficult", "Very difficult"
    val cravingOccurred: Boolean,
    val cravingIntensity: Int = 0, // 0..10
    val note: String? = null
)
