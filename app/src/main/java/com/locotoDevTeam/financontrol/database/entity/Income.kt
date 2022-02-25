package com.locotoDevTeam.financontrol.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Income(
    @PrimaryKey(autoGenerate = true) val uid: Long? = null,
    val name: String,
    val description: String,
    val type: String,
    val categoryId: Long,
    val timestamp: String,
)
