package com.locotoDevTeam.financontrol.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity
data class Income(
    @PrimaryKey(autoGenerate = true) val uid: Long? = null,
    val description: String? = null,
    val type: String,
    val amount: Double,
    val categoryId: Long,
    val timestamp: String,
)
