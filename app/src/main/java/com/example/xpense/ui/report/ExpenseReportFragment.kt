package com.example.xpense.ui.report

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.FileProvider
import androidx.fragment.app.viewModels
import com.example.xpense.R
import com.example.xpense.databinding.FragmentExpenseListBinding
import com.example.xpense.databinding.FragmentExpenseReportBinding
import com.example.xpense.utils.Format
import dagger.hilt.android.AndroidEntryPoint
import java.io.File

@AndroidEntryPoint
class ExpenseReportFragment : Fragment() {

    private var _binding: FragmentExpenseReportBinding? = null
    private val binding get() = _binding!!
    private val viewmodel: ExpenseReportViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, 
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentExpenseReportBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewmodel.load()

        viewmodel.daily.observe(viewLifecycleOwner) { daily ->
            val inFmt = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())
            val outFmt = java.text.SimpleDateFormat("dd MMM", java.util.Locale.getDefault())

            val pairs = daily.map { dt ->
                val date = inFmt.parse(dt.day)
                val label = if (date != null) outFmt.format(date) else dt.day
                label to dt.total
            }
            binding.barChart.setData(pairs)
            binding.barChart.setData(pairs)
            binding.tvDailyTotals.text = daily.joinToString("\n") { "${it.day}: ${Format.money(it.total)}" }
        }

        viewmodel.cats.observe(viewLifecycleOwner) { cats ->
            binding.tvCategoryTotals.text = cats.joinToString("\n") { "${it.category}: ${Format.money(it.total)}" }
        }

        binding.btnExportCsv.setOnClickListener { exportCsvAndShare() }
    }

    private fun exportCsvAndShare() {
        val daily = viewmodel.daily.value.orEmpty()
        val cats = viewmodel.cats.value.orEmpty()

        val sb = StringBuilder()
        sb.appendLine("Type,Key,Amount")
        daily.forEach { sb.appendLine("Daily,${it.day},${it.total}") }
        cats.forEach { sb.appendLine("Category,${it.category},${it.total}") }

        val file = File(requireContext().cacheDir, "expense_report.csv")
        file.writeText(sb.toString())

        val uri: Uri = FileProvider.getUriForFile(
            requireContext(),
            requireContext().packageName + ".fileprovider",
            file
        )
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/csv"
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        startActivity(Intent.createChooser(intent, "Share expense report"))
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}