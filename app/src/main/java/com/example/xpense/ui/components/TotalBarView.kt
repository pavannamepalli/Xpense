package com.example.xpense.ui.components

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import android.widget.TextView
import com.example.xpense.R

class TotalBarView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private val label: TextView
    private val value: TextView

    init {
        inflate(context, R.layout.view_total_bar, this)
        label = findViewById(R.id.totalLabel)
        value = findViewById(R.id.totalValue)
    }

    fun setLabel(text: String) { label.text = text }
    fun setAmount(amount: Double) {
        value.text = context.getString(R.string.total_amount_fmt, amount)
    }
}