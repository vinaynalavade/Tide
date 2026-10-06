package com.tide.app.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "craving_entries",
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
data class CravingEntryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val addictionId: Long,
    val timestamp: Long,
    val intensity: Int, // 1 to 10
    val triggerId: Long? = null,
    val mood: String? = null,
    val resisted: Boolean = true,
    val note: String? = null
)
