package com.example.xpense.utils

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import kotlin.math.max

class BarChartView @JvmOverloads constructor(
    ctx: Context, attrs: AttributeSet? = null
) : View(ctx, attrs) {

    private val bars = mutableListOf<Pair<String, Double>>()
    private val p = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        textSize = 24f
        strokeWidth = 8f
    }

    fun setData(data: List<Pair<String, Double>>) {
        bars.clear(); bars.addAll(data)
        invalidate()
    }

    override fun onDraw(c: Canvas) {
        super.onDraw(c)
        if (bars.isEmpty()) return

        val padding = 32f
        val w = width.toFloat()
        val h = height.toFloat()
        val chartTop = padding * 2
        val chartBottom = h - padding * 3
        val count = bars.size
        val barWidth = (w - padding * 2) / max(1, count) * 0.6f
        val maxVal = bars.maxOf { it.second }.coerceAtLeast(1.0)

        bars.forEachIndexed { index, (label, value) ->
            val xCenter = padding + (index + 0.5f) * ((w - padding * 2) / count)
            val barHeight = ((value / maxVal) * (chartBottom - chartTop)).toFloat()
            val left = xCenter - barWidth/2
            val right = xCenter + barWidth/2
            val top = chartBottom - barHeight
            // Draw bar (default paint color)
            c.drawRect(left, top, right, chartBottom, p)
            // Label
            c.drawText(label, xCenter - p.measureText(label)/2, h - padding, p)
        }
    }
}
