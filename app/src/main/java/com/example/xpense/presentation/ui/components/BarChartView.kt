package com.example.xpense.presentation.ui.components

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.DashPathEffect
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import com.example.xpense.R
import com.google.android.material.color.MaterialColors
import kotlin.math.max
import java.text.NumberFormat
import java.util.Currency
import java.util.Locale
import kotlin.math.absoluteValue
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.pow

class BarChartView @JvmOverloads constructor(
    ctx: Context, 
    attrs: AttributeSet? = null
) : View(ctx, attrs) {

    // Data: X label -> amount
    private val data = mutableListOf<Pair<String, Double>>()

    // Currency formatter (INR, no decimals for axis ticks)
    private val nf: NumberFormat = NumberFormat.getCurrencyInstance(Locale("en", "IN")).apply {
        maximumFractionDigits = 0
        currency = Currency.getInstance("INR")
    }

    // Paints
    private val barPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        color = MaterialColors.getColor(
            this@BarChartView,
            com.google.android.material.R.attr.colorPrimary,
            Color.parseColor("#6366F1")
        )
    }
    
    private val axisPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = MaterialColors.getColor(
            this@BarChartView,
            com.google.android.material.R.attr.colorOnSurface,
            Color.DKGRAY
        )
        strokeWidth = dp(1f)
    }
    
    private val gridPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = axisPaint.color
        alpha = 60
        strokeWidth = dp(1f)
        pathEffect = DashPathEffect(floatArrayOf(dp(4f), dp(4f)), 0f)
    }
    
    private val labelPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = axisPaint.color
        textSize = sp(12f)
    }

    // Tunables
    private var barWidthFraction: Float = 0.6f // 0.1..0.9 (fraction of each slot)
    private var outerPadDp: Float = 6f         // small outer padding
    private var compactYAxis = false           // ₹15k/₹2.5L style axis labels

    /** Update bars (label -> value). Call whenever data changes. */
    fun setData(pairs: List<Pair<String, Double>>) {
        data.clear()
        data.addAll(pairs)
        requestLayout()
        invalidate()
    }

    /** Set primary bar color. */
    fun setBarColor(color: Int) { 
        barPaint.color = color
        invalidate() 
    }

    /** 0.1f..0.9f of slot width (e.g., 0.3f for thinner bars). */
    fun setBarWidthFraction(fraction: Float) {
        barWidthFraction = fraction.coerceIn(0.1f, 0.9f)
        invalidate()
    }

    /** Use compact Y-axis labels (₹15k / ₹2.5L / ₹1.2Cr). */
    fun setCompactYAxisLabels(enabled: Boolean) {
        compactYAxis = enabled
        invalidate()
    }

    /** Adjust outer padding (dp). */
    fun setOuterPaddingDp(padDp: Float) {
        outerPadDp = padDp
        invalidate()
    }

    override fun onDraw(c: Canvas) {
        super.onDraw(c)
        if (data.isEmpty()) {
            val t = context.getString(R.string.chart_no_data)
            c.drawText(t, (width - labelPaint.measureText(t)) / 2f, height / 2f, labelPaint)
            return
        }

        // ----- Compute Y axis "nice" range and ticks for THIS dataset
        val maxVal = data.maxOf { it.second }.coerceAtLeast(1.0)
        val (yMax, step) = niceAxis(maxVal, targetTicks = 4)
        val ticks = buildTicks(yMax, step)
        val yLabels = ticks.map { formatMoney(it) }

        // ----- Measure left margin from actual labels (tight, right-aligned to axis)
        var widest = 0f
        for (s in yLabels) {
            val w = labelPaint.measureText(s)
            if (w > widest) widest = w
        }
        val yLabelPad = dp(4f)
        val outer = dp(outerPadDp)
        val xLabelH = labelPaint.textSize + dp(6f)

        val left = paddingLeft + widest + yLabelPad
        val right = width - paddingRight - outer
        val top = paddingTop + outer
        val bottom = height - paddingBottom - xLabelH
        if (right <= left || bottom <= top) return

        val chartH = (bottom - top).toFloat()
        fun yToPix(v: Double): Float = (bottom - (v / yMax * chartH)).toFloat()

        // ----- Grid + Y axis labels
        for (i in ticks.indices) {
            val y = yToPix(ticks[i])
            c.drawLine(left, y, right, y, gridPaint)
            val lbl = yLabels[i]
            val tx = left - yLabelPad - labelPaint.measureText(lbl) // right-aligned
            c.drawText(lbl, tx, y - dp(2f), labelPaint)
        }

        // ----- Axes
        c.drawLine(left, bottom, right, bottom, axisPaint) // X
        c.drawLine(left, top, left, bottom, axisPaint)     // Y

        // ----- Bars + X labels
        val count = data.size
        val slotW = (right - left) / count.toFloat()
        val barW = (slotW * barWidthFraction).coerceAtLeast(dp(2f))

        data.forEachIndexed { i, (xLabel, value) ->
            val cx = left + slotW * (i + 0.5f)
            val barH = ((value / yMax) * chartH).toFloat()
            val l = cx - barW / 2f
            val r = cx + barW / 2f
            val t = bottom - barH
            c.drawRect(l, t, r, bottom, barPaint)

            // X label centered under bar
            c.drawText(
                xLabel,
                cx - labelPaint.measureText(xLabel) / 2f,
                bottom + labelPaint.textSize + dp(2f),
                labelPaint
            )
        }
    }

    // Build tick values from 0 to max
    private fun buildTicks(max: Double, step: Double): List<Double> {
        val out = ArrayList<Double>()
        var v = 0.0
        while (v <= max + 1e-6) { 
            out += v
            v += step 
        }
        return out
    }

    // Compact INR formatting for Y axis if enabled
    private fun formatMoney(v: Double): String {
        if (!compactYAxis) return nf.format(v)
        val abs = v.absoluteValue
        return when {
            abs >= 1_00_00_000 -> "₹" + String.format(Locale.US, "%.1fCr", abs / 1_00_00_000) // Crore
            abs >= 1_00_000    -> "₹" + String.format(Locale.US, "%.1fL",  abs / 1_00_000)    // Lakh
            abs >= 1_000       -> "₹" + String.format(Locale.US, "%.0fk",  abs / 1_000)       // Thousand
            else               -> nf.format(v)
        }
    }

    // Pick a "nice" axis step using 1/2/5 * 10^n
    private fun niceAxis(maxValue: Double, targetTicks: Int): Pair<Double, Double> {
        if (maxValue <= 0.0) return 1.0 to 1.0
        val rawStep = maxValue / max(1, targetTicks)
        val mag = 10.0.pow(floor(kotlin.math.log10(rawStep)))
        val norm = rawStep / mag
        val niceNorm = when {
            norm <= 1.0 -> 1.0
            norm <= 2.0 -> 2.0
            norm <= 5.0 -> 5.0
            else        -> 10.0
        }
        val step = niceNorm * mag
        val niceMax = ceil(maxValue / step) * step
        return niceMax to step
    }

    private fun dp(v: Float) = v * resources.displayMetrics.density
    private fun sp(v: Float) = v * resources.displayMetrics.scaledDensity
    
}