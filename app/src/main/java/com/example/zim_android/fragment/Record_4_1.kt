package com.example.zim_android.fragment

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.DialogFragment
import com.example.zim_android.databinding.Record41Binding

class Record_4_1(
    private val currentText: String,
    private val onTextConfirmed: (String) -> Unit
) : DialogFragment() {

    private var _binding: Record41Binding? = null
    private val binding get() = _binding!!

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        _binding = Record41Binding.inflate(layoutInflater)

        // 초기값 세팅
        binding.editDescription.setText(currentText)
        binding.editDescription.setSelection(currentText.length)

        // 글자 수 세기
        binding.editDescription.addTextChangedListener {
            val len = it?.length ?: 0
            binding.charCountDescription.text = "$len/56"
        }

        // 취소 버튼
        binding.btnCancelTitle.setOnClickListener {
            dismiss()
        }

        // 확인 버튼
        binding.btnSave.setOnClickListener {
            val newText = binding.editDescription.text.toString().trim()
            if (newText.isNotEmpty()) {
                onTextConfirmed(newText)
                dismiss()
            }
        }

        // 다이얼로그 설정
        val dialog = Dialog(requireContext(), com.example.zim_android.R.style.DialogStyle)
        dialog.setContentView(binding.root)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window?.setDimAmount(0.7f)
        return dialog
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
