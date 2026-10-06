package com.tide.app.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "goals",
    foreignKeys = [
        ForeignKey(
            entity = AddictionEntity::class,
            parentColumns = ["id"],
            childColumns = ["addictionId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("addictionId")]
)
data class GoalEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val addictionId: Long,
    val type: String, // "QUIT", "REDUCE", "TRACK"
    val targetAmount: Double = 0.0,
    val targetFrequency: String = "day",
    val targetDate: Long? = null,
    val active: Boolean = true
)
