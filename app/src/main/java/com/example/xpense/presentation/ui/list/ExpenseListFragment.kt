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
        
        // Initialize with today's expenses and "All" category
        resetToInitialState()
    }

    override fun onResume() {
        super.onResume()
        // Reset to initial state: today's expenses with "All" category
        resetToInitialState()
    }

    private fun resetToInitialState() {
        // Set today's date range
        val todayRange = DateUtils.todayRange()
        vm.setDateRange(todayRange.first, todayRange.second)
        binding.tvDateFilter.text = getString(R.string.today_expenses)
        binding.btnClearDate.visibility = View.GONE
        
        // Reset category to "All"
        binding.inputCategoryFilter.setText(getString(R.string.category_all), false)
        vm.setCategoryOrAll(null)
        binding.inputCategoryFilter.clearFocus()
        binding.root.requestFocus()
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

        // default selection - "All" category
        binding.inputCategoryFilter.setText(getString(R.string.category_all), false)

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
        // Set default to today's date
        val todayRange = DateUtils.todayRange()
        vm.setDateRange(todayRange.first, todayRange.second)
        binding.tvDateFilter.text = getString(R.string.today_expenses)
        binding.btnClearDate.visibility = View.GONE

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

                // Reset category to "All" when date is applied
                binding.inputCategoryFilter.setText(getString(R.string.category_all), false)
                vm.setCategoryOrAll(null)
                binding.inputCategoryFilter.clearFocus()
                binding.root.requestFocus()
            }

            picker.show(parentFragmentManager, getString(R.string.fragment_tag_filter_date))
        }

        binding.btnClearDate.setOnClickListener {
            // Clear date filter and return to today's expenses with "All" category
            resetToInitialState()
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
