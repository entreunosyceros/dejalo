package com.dejalo.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_profile")
data class UserProfileEntity(
    @PrimaryKey val id: Int = 1,
    val quitAtMillis: Long,
    val cigarettesPerDay: Double,
    val packPriceEuros: Double,
    val cigarettesPerPack: Int,
    val motivatorsCsv: String,
    val customMotivator: String = "",
    val savingsGoalEuros: Double = 0.0,
    val savingsGoalLabel: String = "",
    val onboardingCompleted: Boolean = true
)
