package com.example.xpense.data.repository

import com.example.xpense.data.local.ExpenseDao
import com.example.xpense.data.local.ExpenseEntity

class ExpenseRepositoryImpl(private val dao: ExpenseDao) : ExpenseRepository {

    override suspend fun insert(expense: ExpenseEntity) = dao.insert(expense)
    
    override fun getBetween(start: Long, end: Long) = dao.getBetween(start, end)
    
    override fun getTotalBetween(start: Long, end: Long) = dao.getTotalBetween(start, end)
    
    override suspend fun findDuplicate(
        title: String, 
        amount: Double, 
        start: Long, 
        end: Long
    ) = dao.findDuplicate(title, amount, start, end)
    
    override suspend fun getDailyTotals(start: Long, end: Long) = dao.getDailyTotals(start, end)
    
    override suspend fun getCategoryTotals(start: Long, end: Long) = dao.getCategoryTotals(start, end)
    
}