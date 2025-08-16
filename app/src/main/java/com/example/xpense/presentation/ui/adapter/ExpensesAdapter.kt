package com.example.xpense.presentation.ui.adapter

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.xpense.core.utils.DateUtils
import com.example.xpense.core.utils.ExpenseDiff
import com.example.xpense.core.utils.Format
import com.example.xpense.data.local.entity.ExpenseEntity
import com.example.xpense.databinding.ItemExpenseBinding

class ExpensesAdapter : ListAdapter<ExpenseEntity, ExpensesAdapter.ExpenseVH>(ExpenseDiff) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExpenseVH {
        val binding = ItemExpenseBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ExpenseVH(binding)
    }

    override fun onBindViewHolder(holder: ExpenseVH, position: Int) {
        holder.bind(getItem(position))
    }

    class ExpenseVH(private val vb: ItemExpenseBinding) : RecyclerView.ViewHolder(vb.root) {
        fun bind(item: ExpenseEntity) {
            vb.tvTitle.text = item.title
            vb.tvAmount.text = Format.money(item.amount)
            vb.tvCategory.text = item.category
            vb.tvDate.text = DateUtils.formatDate(item.timestamp)

            if (item.imageUri.isNullOrBlank()) {
                vb.ivReceipt.visibility = View.GONE
            } else {
                try {
                    val uri = Uri.parse(item.imageUri)
                    vb.ivReceipt.load(uri) {
                        crossfade(true)
                        listener(
                            onSuccess = { _, _ ->
                                vb.ivReceipt.visibility = View.VISIBLE
                            },
                            onError = { _, _ ->
                                vb.ivReceipt.visibility = View.GONE
                            }
                        )
                    }
                } catch (e: Exception) {
                    vb.ivReceipt.visibility = View.GONE
                }
            }
        }
    }


}