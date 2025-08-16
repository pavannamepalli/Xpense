package com.example.xpense.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.xpense.data.local.ExpenseEntity
import com.example.xpense.databinding.ItemExpenseBinding
import com.example.xpense.utils.DateUtils
import com.example.xpense.utils.Format

class ExpenseAdapter : RecyclerView.Adapter<ExpenseVH>() {

    private val data = mutableListOf<ExpenseEntity>()

    fun submit(new: List<ExpenseEntity>) {
        data.clear()
        data.addAll(new)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ExpenseVH(ItemExpenseBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: ExpenseVH, position: Int) =
        holder.bind(data[position])

    override fun getItemCount() = data.size
    
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
            vb.ivReceipt.visibility = View.VISIBLE
            vb.ivReceipt.setImageURI(android.net.Uri.parse(item.imageUri))
        }
    }
    
}