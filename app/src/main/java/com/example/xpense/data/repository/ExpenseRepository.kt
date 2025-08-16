package com.example.xpense.data.repository

import androidx.lifecycle.LiveData
import com.example.xpense.data.local.CategoryTotal
import com.example.xpense.data.local.DailyTotal
import com.example.xpense.data.local.ExpenseEntity

interface ExpenseRepository {
    
    suspend fun insert(expense: ExpenseEntity): Long
    
    fun getBetween(start: Long, end: Long): LiveData<List<ExpenseEntity>>
    
    fun getTotalBetween(start: Long, end: Long): LiveData<Double?>
    
    suspend fun findDuplicate(
        title: String, 
        amount: Double, 
        start: Long, 
        end: Long
    ): ExpenseEntity?
    
    suspend fun getDailyTotals(start: Long, end: Long): List<DailyTotal>
    
    suspend fun getCategoryTotals(start: Long, end: Long): List<CategoryTotal>
    
}