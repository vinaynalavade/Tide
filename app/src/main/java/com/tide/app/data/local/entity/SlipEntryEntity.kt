package com.tide.app.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "slip_entries",
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
data class SlipEntryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val addictionId: Long,
    val timestamp: Long,
    val amount: Double,
    val cost: Double,
    val triggerId: Long? = null,
    val mood: String? = null,
    val note: String? = null
)
