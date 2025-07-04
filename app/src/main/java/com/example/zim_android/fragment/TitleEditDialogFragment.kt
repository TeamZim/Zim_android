package com.example.zim_android.fragment

import android.app.AlertDialog
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.DialogFragment
import com.example.zim_android.databinding.DialogEditTitleBinding

class TitleEditDialogFragment(
    private val currentTitle: String,
    private val onTitleUpdated: (String) -> Unit
) : DialogFragment() {

    private var _binding: DialogEditTitleBinding? = null
    private val binding get() = _binding!!

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        _binding = DialogEditTitleBinding.inflate(layoutInflater)

        binding.editTitle1.setText(currentTitle)
        binding.editTitle1.setSelection(currentTitle.length)

        // 글자 수 표시
        binding.editTitle1.addTextChangedListener {
            val length = it?.length ?: 0
            binding.charCountTitle.text = "$length/15"
        }

        val builder = AlertDialog.Builder(requireContext(), com.example.zim_android.R.style.DialogStyle)
            .setView(binding.root)
            .setPositiveButton("확인") { _, _ ->
                val newTitle = binding.editTitle1.text.toString()
                onTitleUpdated(newTitle)
            }
            .setNegativeButton("취소", null)

        return builder.create()
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setDimAmount(0.7f)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
