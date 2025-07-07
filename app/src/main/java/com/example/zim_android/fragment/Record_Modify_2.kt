package com.example.zim_android.fragment

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.DialogFragment
import com.example.zim_android.databinding.RecordModify2Binding

class Record_Modify_2(
    private val currentTitle: String,
    private val onTitleUpdated: (String) -> Unit
) : DialogFragment() {

    private var _binding: RecordModify2Binding? = null
    private val binding get() = _binding!!

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        _binding = RecordModify2Binding.inflate(layoutInflater)

        // 초기값 세팅
        binding.editDescription.setText(currentTitle)
        binding.editDescription.setSelection(currentTitle.length)

        // 글자 수 실시간 업데이트
        binding.editDescription.addTextChangedListener {
            val length = it?.length ?: 0
            binding.charCountDescription.text = "$length/56"
        }
        // 취소 버튼: 다이얼로그 종료
        binding.btnCancelTitle.setOnClickListener {
            dismiss()
        }

        // 확인 버튼: 제목 전달 & 종료
        binding.btnConfirmText.setOnClickListener {
            val newTitle = binding.editDescription.text.toString().trim()
            if (newTitle.isNotEmpty()) {
                onTitleUpdated(newTitle)
                dismiss()
            }
        }

        // 다이얼로그 커스텀 스타일 적용
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
