package com.example.xpense.core.utils

import androidx.recyclerview.widget.DiffUtil
import com.example.xpense.data.local.entity.ExpenseEntity

object ExpenseDiff : DiffUtil.ItemCallback<ExpenseEntity>() {
    override fun areItemsTheSame(oldItem: ExpenseEntity, newItem: ExpenseEntity): Boolean {
        // Compare unique IDs
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: ExpenseEntity, newItem: ExpenseEntity): Boolean {
        // Compare full content
        return oldItem == newItem
    }
}