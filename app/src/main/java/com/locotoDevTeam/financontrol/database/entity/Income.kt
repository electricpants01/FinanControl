package com.locotoDevTeam.financontrol.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Income(
    @PrimaryKey(autoGenerate = true) val uid: Long? = null,
    val description: String? = null,
    // just exists 2 types: Income, Expense
    val type: String,
    val amount: Double,
    val categoryId: Long,
    val timestamp: String,
)
