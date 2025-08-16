package com.example.xpense.ui.report

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.xpense.data.local.CategoryTotal
import com.example.xpense.data.local.DailyTotal
import com.example.xpense.data.repository.ExpenseRepository
import com.example.xpense.utils.DateUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ExpenseReportViewModel @Inject constructor(
    private val repo: ExpenseRepository
) : ViewModel() {

    private val _daily = MutableLiveData<List<DailyTotal>>(emptyList())
    val daily: LiveData<List<DailyTotal>> = _daily

    private val _cats = MutableLiveData<List<CategoryTotal>>(emptyList())
    val cats: LiveData<List<CategoryTotal>> = _cats

    fun load() {
        viewModelScope.launch {
            val (start, end) = DateUtils.lastNDaysRange(7)
            _daily.value = repo.getDailyTotals(start, end)
            _cats.value = repo.getCategoryTotals(start, end)
        }
    }
    
}
