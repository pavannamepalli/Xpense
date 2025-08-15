package com.example.xpense.ui.report

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.xpense.R
import com.example.xpense.databinding.FragmentExpenseListBinding
import com.example.xpense.databinding.FragmentExpenseReportBinding


class ExpenseReportFragment : Fragment() {

    private lateinit var binding: FragmentExpenseReportBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentExpenseReportBinding.inflate(inflater, container, false)
        return binding.root
    }


}