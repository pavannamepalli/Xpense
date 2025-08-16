package com.example.xpense.data.local

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface ExpenseDao {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(expense: ExpenseEntity): Long

    @Query("""
        SELECT * FROM expenses 
        WHERE timestamp BETWEEN :start AND :end 
        ORDER BY timestamp DESC
    """)
    fun getBetween(start: Long, end: Long): LiveData<List<ExpenseEntity>>

    @Query("""
        SELECT SUM(amount) FROM expenses 
        WHERE timestamp BETWEEN :start AND :end
    """)
    fun getTotalBetween(start: Long, end: Long): LiveData<Double?>


    @Query("""
        SELECT * FROM expenses
        WHERE lower(title) = lower(:title) AND amount = :amount
        AND timestamp BETWEEN :start AND :end
        LIMIT 1
    """)
    suspend fun findDuplicate(title: String, amount: Double, start: Long, end: Long): ExpenseEntity?


    @Query("""
        SELECT strftime('%Y-%m-%d', datetime(timestamp/1000,'unixepoch','localtime')) AS day,
               IFNULL(SUM(amount), 0) AS total
        FROM expenses
        WHERE timestamp BETWEEN :start AND :end
        GROUP BY day
        ORDER BY day ASC
    """)
    suspend fun getDailyTotals(start: Long, end: Long): List<DailyTotal>


    @Query("""
        SELECT category AS category, IFNULL(SUM(amount), 0) AS total
        FROM expenses
        WHERE timestamp BETWEEN :start AND :end
        GROUP BY category
        ORDER BY total DESC
    """)
    suspend fun getCategoryTotals(start: Long, end: Long): List<CategoryTotal>
}
