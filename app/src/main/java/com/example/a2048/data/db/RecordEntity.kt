package com.example.a2048.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "records")
data class RecordEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val score: Int,
    val size: Int,
    val createdAt: Long
)
