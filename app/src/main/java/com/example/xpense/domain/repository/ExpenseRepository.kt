package com.example.xpense.domain.repository

import androidx.lifecycle.LiveData
import com.example.xpense.data.model.CategoryTotal
import com.example.xpense.data.model.DailyTotal
import com.example.xpense.data.local.entity.ExpenseEntity

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