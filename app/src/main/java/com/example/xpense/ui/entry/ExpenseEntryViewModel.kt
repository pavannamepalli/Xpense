package com.example.xpense.ui.entry

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.xpense.data.local.ExpenseEntity
import com.example.xpense.data.repository.ExpenseRepository
import com.example.xpense.utils.DateUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ExpenseEntryViewModel @Inject constructor(
    private val repo: ExpenseRepository,
) : ViewModel() {


    val todayTotal: LiveData<Double?> = repo.getTotalBetween(
        DateUtils.startOfDay(), DateUtils.endOfDay()
    )

    fun addExpense(
        title: String,
        amountStr: String,
        category: String,
        timestamp: Long,
        notes: String?,
        imageUri: String?,
        onResult: (success: Boolean, message: String) -> Unit
    ) {
        viewModelScope.launch(Dispatchers.IO) {

            val amount = amountStr.toDoubleOrNull() ?: 0.0
            if (title.isBlank()) {
                onResult(false, "Title cannot be empty"); return@launch
            }
            if (amount <= 0.0) {
                onResult(false, "Amount must be greater than 0"); return@launch
            }


            val start = DateUtils.startOfDay(timestamp)
            val end = DateUtils.endOfDay(timestamp)
            val exists = repo.findDuplicate(title.trim(), amount, start, end)
            if (exists != null) {
                onResult(false, "Duplicate expense detected for same day"); return@launch
            }


            val id = repo.insert(
                ExpenseEntity(
                    title = title.trim(),
                    amount = amount,
                    category = category,
                    timestamp = timestamp,
                    notes = notes?.take(100),
                    imageUri = imageUri
                )
            )
            onResult(id > 0, if (id > 0) "Saved!" else "Insert failed")
        }
    }
}
