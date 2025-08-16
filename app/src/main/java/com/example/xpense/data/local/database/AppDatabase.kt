package com.example.xpense.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.xpense.data.local.entity.ExpenseEntity
import com.example.xpense.data.local.dao.ExpenseDao

@Database(
    entities = [ExpenseEntity::class],
    version = 1, 
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    
    abstract fun expenseDao(): ExpenseDao
    
}