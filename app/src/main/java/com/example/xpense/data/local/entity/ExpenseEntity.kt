package com.example.xpense.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "expenses")
data class ExpenseEntity(
    @PrimaryKey(autoGenerate = true) 
    val id: Long = 0L,
    val title: String,
    val amount: Double,
    val category: String,
    val timestamp: Long,
    val notes: String? = null,
    val imageUri: String? = null
)
