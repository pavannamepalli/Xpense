package com.example.xpense.ui.report

import android.content.Intent
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.FileProvider
import androidx.fragment.app.viewModels
import com.example.xpense.R
import com.example.xpense.data.local.CategoryTotal
import com.example.xpense.data.local.DailyTotal
import com.example.xpense.databinding.FragmentExpenseListBinding
import com.example.xpense.databinding.FragmentExpenseReportBinding
import com.example.xpense.utils.Format
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
import java.io.FileOutputStream
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Currency
import java.util.Locale

@AndroidEntryPoint
class ExpenseReportFragment : Fragment() {

    private var _binding: FragmentExpenseReportBinding? = null
    private val binding get() = _binding!!
    private val viewmodel: ExpenseReportViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, 
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentExpenseReportBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewmodel.load()

        viewmodel.daily.observe(viewLifecycleOwner) { daily ->

            val totalsByDay: Map<String, Double> = daily.associate { it.day to it.total }

            val inFmt = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val outFmt = SimpleDateFormat("dd MMM", Locale.getDefault())

            val pairs = ArrayList<Pair<String, Double>>(7)

            // Oldest to newest: last 7 days including today
            val cal = java.util.Calendar.getInstance()
            // normalize to today (optional)
            cal.set(java.util.Calendar.HOUR_OF_DAY, 0)
            cal.set(java.util.Calendar.MINUTE, 0)
            cal.set(java.util.Calendar.SECOND, 0)
            cal.set(java.util.Calendar.MILLISECOND, 0)

            for (i in 6 downTo 0) {
                val d = cal.clone() as java.util.Calendar
                d.add(java.util.Calendar.DAY_OF_YEAR, -i)
                val key   = inFmt.format(d.time)        // "yyyy-MM-dd" to match DailyTotal.day
                val label = outFmt.format(d.time)       // "16 Aug" etc.
                val amount = totalsByDay[key] ?: 0.0    // fill missing days with 0
                pairs += label to amount
            }
            binding.barChart.setData(pairs)
            binding.barChart.setBarWidthFraction(0.30f)
            binding.barChart.setCompactYAxisLabels(true)
            binding.tvDailyTotals.text = daily.joinToString("\n") { "${it.day}: ${Format.money(it.total)}" }
        }

        viewmodel.cats.observe(viewLifecycleOwner) { cats ->
            binding.tvCategoryTotals.text = cats.joinToString("\n") { "${it.category}: ${Format.money(it.total)}" }
        }

        binding.btnExportCsv.setOnClickListener { exportPdfAndShare() }
    }



    private fun exportPdfAndShare() {
        val daily: List<DailyTotal> = viewmodel.daily.value.orEmpty()
        val cats: List<CategoryTotal> = viewmodel.cats.value.orEmpty() // if you use pairs, see note below

        val money = NumberFormat.getCurrencyInstance(Locale("en", "IN")).apply {
            maximumFractionDigits = 2
            currency = Currency.getInstance("INR")
        }
        val inFmt  = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val outFmt = SimpleDateFormat("dd MMM",    Locale.getDefault())

        // A4 @ 72dpi
        val pageW = 595
        val pageH = 842
        val left   = 40f
        val top    = 50f
        val bottom = (pageH - 50).toFloat()
        val line   = 18f

        val titlePaint  = Paint(Paint.ANTI_ALIAS_FLAG).apply { textSize = 18f; isFakeBoldText = true }
        val headerPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { textSize = 14f; isFakeBoldText = true }
        val rowPaint    = Paint(Paint.ANTI_ALIAS_FLAG).apply { textSize = 12f }

        val doc = PdfDocument()
        var pageNum = 1
        var page = doc.startPage(PdfDocument.PageInfo.Builder(pageW, pageH, pageNum).create())
        var c = page.canvas
        var x = left
        var y = top

        fun newPage() {
            doc.finishPage(page)
            pageNum++
            page = doc.startPage(PdfDocument.PageInfo.Builder(pageW, pageH, pageNum).create())
            c = page.canvas
            x = left; y = top
        }
        fun need(lines: Int) { if (y + lines * line > bottom) newPage() }

        // Title
        c.drawText(getString(R.string.pdf_title), x, y, titlePaint); y += line * 1.2f

        // Daily totals
        if (daily.isNotEmpty()) {
            need(1); c.drawText(getString(R.string.pdf_daily_totals), x, y, headerPaint); y += line
            daily.forEach { d ->
                need(1)
                val label = runCatching { outFmt.format(inFmt.parse(d.day)!!) }.getOrElse { d.day }
                c.drawText("$label : ${money.format(d.total)}", x, y, rowPaint)
                y += line
            }
            y += line * 0.5f
        }

        // Category totals
        if (cats.isNotEmpty()) {
            need(1); c.drawText(getString(R.string.pdf_category_totals), x, y, headerPaint); y += line
            cats.forEach { ct ->
                need(1)
                c.drawText("${ct.category} : ${money.format(ct.total)}", x, y, rowPaint)
                y += line
            }
        }

        doc.finishPage(page)

        val file = File(requireContext().cacheDir, getString(R.string.pdf_file_name))
        FileOutputStream(file).use { out -> doc.writeTo(out) }
        doc.close()

        val uri: Uri = FileProvider.getUriForFile(
            requireContext(),
            "${requireContext().packageName}.fileprovider",
            file
        )
        
        // Show dialog with options to open or share PDF
        showPdfOptionsDialog(uri)
    }
    
    private fun showPdfOptionsDialog(uri: Uri) {
        val options = arrayOf(
            getString(R.string.btn_open_pdf),
            getString(R.string.btn_share_pdf)
        )
        
        androidx.appcompat.app.AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.dialog_pdf_options_title))
            .setItems(options) { _, which ->
                when (which) {
                    0 -> openPdf(uri)  // Open PDF
                    1 -> sharePdf(uri) // Share PDF
                }
            }
            .setNegativeButton(getString(R.string.btn_cancel)) { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }
    
    private fun openPdf(uri: Uri) {
        try {
            val openPdfIntent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(uri, getString(R.string.mime_type_pdf))
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            startActivity(openPdfIntent)
            Toast.makeText(requireContext(), getString(R.string.msg_pdf_opening), Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Toast.makeText(requireContext(), getString(R.string.msg_no_pdf_viewer), Toast.LENGTH_LONG).show()
        }
    }
    
    private fun sharePdf(uri: Uri) {
        val share = Intent(Intent.ACTION_SEND).apply {
            type = getString(R.string.mime_type_pdf)
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        startActivity(Intent.createChooser(share, getString(R.string.share_expense_report_pdf)))
        Toast.makeText(requireContext(), getString(R.string.msg_pdf_sharing), Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}