package com.example.xpense.presentation.ui.entry

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.OvershootInterpolator
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import coil.load
import com.example.xpense.R
import com.example.xpense.databinding.FragmentExpenseEntryBinding
import com.example.xpense.core.utils.DateUtils
import com.example.xpense.core.utils.Format
import com.example.xpense.presentation.viewmodel.ExpenseEntryViewModel
import com.google.android.material.datepicker.MaterialDatePicker
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.io.File

@AndroidEntryPoint
class ExpenseEntryFragment : Fragment() {

    private var _binding: FragmentExpenseEntryBinding? = null
    private val binding get() = _binding!!

    private val viewmodel: ExpenseEntryViewModel by viewModels()

    private var pickedDateMillis = System.currentTimeMillis()
    private var pickedImageUri: Uri? = null

    /* ---------------- Permission + Image Picking ---------------- */

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            pickImage.launch(getString(R.string.mime_type_image))
        } else {
            Toast.makeText(
                requireContext(),
                getString(R.string.msg_permission_required),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private val pickImage = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->
        try {
            if (uri != null) {
                val internalUri = copyImageToInternalStorage(uri)
                pickedImageUri = internalUri
                binding.ivReceipt.load(internalUri)
                binding.ivReceipt.visibility = View.VISIBLE
            } else {
                pickedImageUri = null
                binding.ivReceipt.visibility = View.GONE
            }
        } catch (e: Exception) {
            Toast.makeText(
                requireContext(),
                getString(R.string.msg_image_load_error, e.message),
                Toast.LENGTH_SHORT
            ).show()
            binding.ivReceipt.visibility = View.GONE
        }
    }

    private fun copyImageToInternalStorage(uri: Uri): Uri {
        val inputStream = requireContext().contentResolver.openInputStream(uri)
        val fileName = "receipt_${System.currentTimeMillis()}.jpg"
        val outputFile = File(requireContext().filesDir, fileName)

        inputStream?.use { input ->
            outputFile.outputStream().use { output ->
                input.copyTo(output)
            }
        }
        
        // Use FileProvider for more reliable access
        return androidx.core.content.FileProvider.getUriForFile(
            requireContext(),
            "${requireContext().packageName}.fileprovider",
            outputFile
        )
    }

    private fun checkPermissionAndPickImage() {
        val permission =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                Manifest.permission.READ_MEDIA_IMAGES
            } else {
                Manifest.permission.READ_EXTERNAL_STORAGE
            }

        when {
            ContextCompat.checkSelfPermission(requireContext(), permission) ==
                    PackageManager.PERMISSION_GRANTED -> {
                pickImage.launch(getString(R.string.mime_type_image))
            }
            shouldShowRequestPermissionRationale(permission) -> {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.msg_permission_required),
                    Toast.LENGTH_SHORT
                ).show()
            }
            else -> requestPermissionLauncher.launch(permission)
        }
    }

    /* ---------------- Lifecycle ---------------- */

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentExpenseEntryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setupCategoryDropdown()
        setupDatePicker()
        setupObservers()
        setupActions()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        cleanupPickedImage()
        _binding = null
    }

    /* ---------------- Setup Methods ---------------- */

    private fun setupCategoryDropdown() {
        val categories = listOf(
            getString(R.string.category_staff),
            getString(R.string.category_travel),
            getString(R.string.category_food),
            getString(R.string.category_utility)
        )
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, categories)
        binding.inputCategory.setAdapter(adapter)
        binding.inputCategory.threshold = 0

        binding.inputCategory.setOnClickListener {
            binding.inputCategory.showDropDown()
        }

        binding.inputCategory.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus) binding.inputCategory.showDropDown()
        }

        binding.inputCategory.setText("", false)
    }

    override fun onResume() {
        super.onResume()
        setupCategoryDropdown()
    }

    private fun setupDatePicker() {
        binding.tilDate.setEndIconOnClickListener { openDatePicker() }
        binding.inputDate.setOnClickListener { openDatePicker() }
        binding.inputDate.setText(DateUtils.formatDate(pickedDateMillis))
    }

    private fun setupObservers() {
        viewmodel.todayTotal.observe(viewLifecycleOwner) { total ->
            binding.tvTodayAmount.text = Format.money(total)
        }
    }

    private fun setupActions() {
        binding.btnPickImage.setOnClickListener {
            checkPermissionAndPickImage()
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

    /* ---------------- Animations ---------------- */

    private fun popSubmit() {
        binding.btnSubmit.animate()
            .scaleX(0.96f).scaleY(0.96f)
            .setDuration(70)
            .withEndAction {
                binding.btnSubmit.animate()
                    .scaleX(1f).scaleY(1f)
                    .setDuration(120)
                    .setInterpolator(OvershootInterpolator())
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
                    .setInterpolator(OvershootInterpolator())
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

    /* ---------------- Helpers ---------------- */

    private fun openDatePicker() {
        val picker = MaterialDatePicker.Builder.datePicker()
            .setTitleText(getString(R.string.date_picker_title))
            .setSelection(pickedDateMillis)
            .build()

        picker.addOnPositiveButtonClickListener { millis ->
            pickedDateMillis = millis
            binding.inputDate.setText(DateUtils.formatDate(millis))
        }
        picker.show(parentFragmentManager, getString(R.string.fragment_tag_date))
    }

    private fun clearFormKeepingCategoryAndDate() {
        binding.inputTitle.text = null
        binding.inputAmount.text = null
        binding.inputNotes.text = null

        pickedImageUri?.let { uri ->
            try {
                if (uri.scheme == "file") {
                    val file = File(uri.path!!)
                    if (file.exists()) file.delete()
                }
            } catch (_: Exception) {
            }
        }

        pickedImageUri = null
        binding.inputCategory.setText("", false)
        binding.inputCategory.clearFocus()

        binding.ivReceipt.setImageDrawable(null)
        binding.ivReceipt.visibility = View.GONE
    }

    private fun cleanupPickedImage() {
        pickedImageUri?.let { uri ->
            try {
                if (uri.scheme == "file") {
                    val file = File(uri.path!!)
                    if (file.exists()) file.delete()
                }
            } catch (_: Exception) {
            }
        }
    }
}
