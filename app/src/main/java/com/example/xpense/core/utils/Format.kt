package com.example.xpense.core.utils

import java.text.NumberFormat
import java.util.Currency
import java.util.Locale

object Format {
    
    private val currency = NumberFormat.getCurrencyInstance(Locale("en", "IN")).apply {
        maximumFractionDigits = 2
        currency = Currency.getInstance("INR")
    }
    
    fun money(amount: Double?): String = currency.format(amount ?: 0.0)
    
}