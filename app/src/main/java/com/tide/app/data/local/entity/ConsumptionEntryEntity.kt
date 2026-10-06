package com.tide.app.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "consumption_entries",
    foreignKeys = [
        ForeignKey(
            entity = AddictionEntity::class,
            parentColumns = ["id"],
            childColumns = ["addictionId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("addictionId"), Index("timestamp")]
)
data class ConsumptionEntryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val addictionId: Long,
    val timestamp: Long,
    val amount: Double,
    val calculatedCost: Double,
    val triggerId: Long? = null,
    val note: String? = null
)
