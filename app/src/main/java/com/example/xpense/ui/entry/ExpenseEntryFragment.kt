package com.example.xpense.ui.entry

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.xpense.R
import com.example.xpense.databinding.FragmentExpenseEntryBinding


class ExpenseEntryFragment : Fragment() {

    private lateinit var binding: FragmentExpenseEntryBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentExpenseEntryBinding.inflate(inflater,container,false)
       return binding.root
    }



}