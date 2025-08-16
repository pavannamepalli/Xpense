package com.example.xpense.presentation.ui.list

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.xpense.R
import com.example.xpense.databinding.FragmentExpenseListBinding
import com.example.xpense.core.utils.DateUtils
import com.example.xpense.core.utils.Format
import com.example.xpense.presentation.ui.adapter.ExpensesAdapter
import com.example.xpense.presentation.viewmodel.ExpenseListViewModel
import com.google.android.material.datepicker.MaterialDatePicker
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ExpenseListFragment : Fragment() {

    private var _binding: FragmentExpenseListBinding? = null
    private val binding get() = _binding!!

    private val vm: ExpenseListViewModel by viewModels()
    private val adapter = ExpensesAdapter()

    private lateinit var categories: Array<String>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentExpenseListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setupCategories()
        setupCategoryDropdown()
        setupDateFilter()
        setupRecycler()
        setupObservers()
    }

    override fun onResume() {
        super.onResume()
        // 🔥 Fix: Reset adapter so dropdown always shows all categories
        setupCategoryDropdown()
        binding.inputCategoryFilter.setText(getString(R.string.category_all), false)
        vm.setCategoryOrAll(null)
    }

    private fun setupCategories() {
        categories = arrayOf(
            getString(R.string.category_all),
            getString(R.string.category_staff),
            getString(R.string.category_travel),
            getString(R.string.category_food),
            getString(R.string.category_utility)
        )
    }

    private fun setupCategoryDropdown() {
        val categoryAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_list_item_1,
            categories
        )
        binding.inputCategoryFilter.setAdapter(categoryAdapter)
        binding.inputCategoryFilter.threshold = 0

        // show dropdown on click
        binding.inputCategoryFilter.setOnClickListener {
            binding.inputCategoryFilter.showDropDown()
        }

        // default selection
        binding.inputCategoryFilter.setText(categories[0], false)

        // item selection handling
        binding.inputCategoryFilter.setOnItemClickListener { _, _, pos, _ ->
            val selected = categories[pos]
            vm.setCategoryOrAll(
                if (selected == getString(R.string.category_all)) null else selected
            )
            binding.inputCategoryFilter.clearFocus()
            binding.root.requestFocus()
        }
    }

    private fun setupDateFilter() {
        binding.btnPickDate.setOnClickListener {
            val picker = MaterialDatePicker.Builder.datePicker()
                .setTitleText(getString(R.string.date_picker_filter_title))
                .build()

            picker.addOnPositiveButtonClickListener { millis ->
                val start = DateUtils.startOfDay(millis)
                val end = DateUtils.endOfDay(millis)
                vm.setDateRange(start, end)
                binding.tvDateFilter.text = DateUtils.formatDate(millis)
                binding.btnClearDate.visibility = View.VISIBLE

                // Reset category when date is applied
                binding.inputCategoryFilter.setText(getString(R.string.category_all), false)
                vm.setCategoryOrAll(null)
                binding.inputCategoryFilter.clearFocus()
                binding.root.requestFocus()
            }

            picker.show(parentFragmentManager, getString(R.string.fragment_tag_filter_date))
        }

        binding.btnClearDate.setOnClickListener {
            val (start, end) = DateUtils.lastNDaysRange(365)
            vm.setDateRange(start, end)
            binding.tvDateFilter.text = getString(R.string.all_expenses)
            binding.btnClearDate.visibility = View.GONE

            // Reset category when clearing date
            binding.inputCategoryFilter.setText(getString(R.string.category_all), false)
            vm.setCategoryOrAll(null)
            binding.inputCategoryFilter.clearFocus()
            binding.root.requestFocus()
        }
    }

    private fun setupRecycler() {
        binding.recycler.layoutManager = LinearLayoutManager(requireContext())
        binding.recycler.adapter = adapter
    }

    private fun setupObservers() {
        vm.expenses.observe(viewLifecycleOwner) { list ->
            adapter.submitList(list)
            binding.groupEmpty.isVisible = list.isEmpty()
            binding.recycler.isVisible = list.isNotEmpty()
        }

        vm.totalCount.observe(viewLifecycleOwner) { count ->
            binding.tvTotalCount.text = getString(R.string.total_count_fmt, count)
        }

        vm.totalAmount.observe(viewLifecycleOwner) { amount ->
            binding.tvTotalAmount.text =
                getString(R.string.total_amount_fmt, Format.money(amount))
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
