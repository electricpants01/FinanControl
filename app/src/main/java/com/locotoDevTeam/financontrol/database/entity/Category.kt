package com.locotoDevTeam.financontrol.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Category(
    @PrimaryKey(autoGenerate = true) val uid: Long? = null,
    val name: String
)