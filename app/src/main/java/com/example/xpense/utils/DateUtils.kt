package com.example.xpense.utils

import java.util.Calendar
import java.text.SimpleDateFormat
import java.util.*

object DateUtils {
    
    private val dayFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    private val dateTimeFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())

    fun startOfDay(millis: Long = System.currentTimeMillis()): Long {
        val cal = Calendar.getInstance().apply { 
            timeInMillis = millis
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        return cal.timeInMillis
    }

    fun endOfDay(millis: Long = System.currentTimeMillis()): Long {
        val cal = Calendar.getInstance().apply { 
            timeInMillis = millis
            set(Calendar.HOUR_OF_DAY, 23)
            set(Calendar.MINUTE, 59)
            set(Calendar.SECOND, 59)
            set(Calendar.MILLISECOND, 999)
        }
        return cal.timeInMillis
    }

    fun lastNDaysRange(days: Int): Pair<Long, Long> {
        val end = endOfDay()
        val startCal = Calendar.getInstance().apply {
            add(Calendar.DAY_OF_YEAR, -(days - 1))
        }
        val start = startOfDay(startCal.timeInMillis)
        return start to end
    }

    fun formatDayKey(key: String): String = key
    
    fun formatDate(millis: Long): String = dateTimeFormat.format(Date(millis))
    
}
