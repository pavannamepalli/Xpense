package com.example.xpense.ui.entry

import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.OvershootInterpolator
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.xpense.R
import com.example.xpense.databinding.FragmentExpenseEntryBinding
import com.example.xpense.utils.DateUtils
import com.example.xpense.utils.Format
import com.google.android.material.datepicker.MaterialDatePicker
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


@AndroidEntryPoint
class ExpenseEntryFragment : Fragment() {

    private var _binding: FragmentExpenseEntryBinding? =null
    private val binding get() = _binding!!

    private val viewmodel: ExpenseEntryViewModel by viewModels()

    private var pickedDateMillis = System.currentTimeMillis()
    private var pickedImageUri: Uri? = null

    private val pickImage = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        pickedImageUri = uri
        binding.ivReceipt.setImageURI(uri)
        binding.ivReceipt.visibility = if (uri == null) View.GONE else View.VISIBLE
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentExpenseEntryBinding.inflate(inflater,container,false)
       return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        val categories = listOf("Staff", "Travel", "Food", "Utility")
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, categories)
        binding.inputCategory.setAdapter(adapter)
        binding.inputCategory.threshold = 0

        binding.inputCategory.setOnClickListener {
            binding.inputCategory.setText("", false)
            binding.inputCategory.showDropDown()
        }


        binding.tilDate.setEndIconOnClickListener { openDatePicker() }
        binding.inputDate.setOnClickListener { openDatePicker() }
        binding.inputDate.setText(DateUtils.formatDate(pickedDateMillis))


        binding.btnPickImage.setOnClickListener { pickImage.launch("image/*") }


       viewmodel.todayTotal.observe(viewLifecycleOwner) { total ->
            binding.tvTodayTotal.text = getString(R.string.today_total_fmt, Format.money(total))
        }


        binding.btnSubmit.setOnClickListener {
            popSubmit()
           viewmodel.addExpense(
                title = binding.inputTitle.text?.toString().orEmpty(),
                amountStr = binding.inputAmount.text?.toString().orEmpty(),
                category = binding.inputCategory.text?.toString().orEmpty(),
                timestamp = pickedDateMillis,
                notes = binding.inputNotes.text?.toString(),
                imageUri = pickedImageUri?.toString()
            ) { success, _ ->
                viewLifecycleOwner.lifecycleScope.launch {
                    if (success) {
                        animateSuccess()
                        showSuccessOverlay()
                        binding.inputCategory.clearFocus()
                        binding.container.requestFocus()
                        clearFormKeepingCategoryAndDate()
                    }
                }
            }
        }
    }

    private fun popSubmit() {
        binding.btnSubmit.animate()
            .scaleX(0.96f).scaleY(0.96f)
            .setDuration(70)
            .withEndAction {
                binding.btnSubmit.animate()
                    .scaleX(1f).scaleY(1f)
                    .setDuration(120)
                    .setInterpolator(android.view.animation.OvershootInterpolator())
                    .start()
            }.start()
    }

    private fun showSuccessOverlay() {
        val overlay = binding.successOverlay
        val card = binding.successCard

        overlay.alpha = 0f
        card.scaleX = 0.9f
        card.scaleY = 0.9f
        overlay.visibility = View.VISIBLE

        overlay.animate()
            .alpha(1f)
            .setDuration(140)
            .withEndAction {
                card.animate()
                    .scaleX(1f).scaleY(1f)
                    .setDuration(180)
                    .setInterpolator(android.view.animation.OvershootInterpolator())
                    .withEndAction {
                        card.postDelayed({
                            overlay.animate()
                                .alpha(0f)
                                .setDuration(160)
                                .withEndAction { overlay.visibility = View.GONE }
                                .start()
                        }, 900)
                    }
                    .start()
            }
            .start()
    }
    private fun openDatePicker() {
        val picker = MaterialDatePicker.Builder.datePicker()
            .setTitleText("Select date")
            .setSelection(pickedDateMillis)
            .build()
        picker.addOnPositiveButtonClickListener { millis ->
            pickedDateMillis = millis
            binding.inputDate.setText(DateUtils.formatDate(millis))
        }
        picker.show(parentFragmentManager, "date")
    }

    private fun animateSuccess() {
        binding.cardForm.animate()
            .scaleX(1.04f).scaleY(1.04f)
            .setDuration(120)
            .withEndAction {
                binding.cardForm.animate()
                    .scaleX(1f).scaleY(1f)
                    .setDuration(200)
                    .setInterpolator(OvershootInterpolator())
                    .start()
            }.start()
    }

    private fun clearFormKeepingCategoryAndDate() {
        binding.inputTitle.text = null
        binding.inputAmount.text = null
        binding.inputNotes.text = null
        pickedImageUri = null
        binding.inputCategory.setText("",false)
        binding.ivReceipt.setImageDrawable(null)
        binding.ivReceipt.visibility = View.GONE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}