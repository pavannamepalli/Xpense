package com.example.xpense.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import androidx.lifecycle.switchMap
import com.example.xpense.data.local.entity.ExpenseEntity
import com.example.xpense.domain.repository.ExpenseRepository
import com.example.xpense.core.utils.DateUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ExpenseListViewModel @Inject constructor(
    private val repo: ExpenseRepository
) : ViewModel() {
    
    private val _dateRange = MutableLiveData(DateUtils.todayRange())
    private val _category = MutableLiveData<String?>(null)

    val source = _dateRange.switchMap { (start, end) ->
        repo.getBetween(start, end)
    }

    private val _expenses = MediatorLiveData<List<ExpenseEntity>>()
    val expenses: LiveData<List<ExpenseEntity>> get() = _expenses

    init {
        var base: List<ExpenseEntity> = emptyList()
        var cat: String? = _category.value

        fun publish() {
            _expenses.value = if (cat.isNullOrBlank()) {
                base
            } else {
                val wanted = cat!!.trim()
                base.filter { it.category.equals(wanted, ignoreCase = true) }
            }
        }

        _expenses.addSource(source) { items: List<ExpenseEntity> ->
            base = items
            publish()
        }
        
        _expenses.addSource(_category) { c: String? ->
            cat = c
            publish()
        }
    }

    fun setDateRange(start: Long, end: Long) { 
        _dateRange.value = start to end 
    }
    
    fun setCategoryOrAll(cat: String?) { 
        _category.value = cat 
    }

    val totalCount: LiveData<Int> = expenses.map { it.size }
    val totalAmount: LiveData<Double> = expenses.map { list -> list.sumOf { it.amount } }
    
}
