package com.example.xpense.ui.list

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.xpense.R
import com.example.xpense.databinding.FragmentExpenseEntryBinding
import com.example.xpense.databinding.FragmentExpenseListBinding


class ExpenseListFragment : Fragment() {

    private lateinit var binding: FragmentExpenseListBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentExpenseListBinding.inflate(inflater, container, false)
        return binding.root
    }


}