package com.example.a2048.domain.model

data class GameRecord(
    val id: Long = 0,
    val score: Int,
    val size: Int,
    val createdAt: Long
)
