package com.example.zim_android.fragment

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.EditText
import android.widget.TextView
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.DialogFragment
import com.example.zim_android.R
import android.graphics.Color
import android.graphics.drawable.ColorDrawable

class TitleEditDialogFragment(
    private val currentTitle: String,
    private val onTitleUpdated: (String) -> Unit
) : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_edit_title, null)
        val editText = dialogView.findViewById<EditText>(R.id.edit_title_1)
        val charCount = dialogView.findViewById<TextView>(R.id.char_count_title)

        editText.setText(currentTitle)
        editText.setSelection(currentTitle.length)

        // 글자 수 표시
        editText.addTextChangedListener {
            charCount.text = "${it?.length ?: 0}/15"
        }

        val builder = AlertDialog.Builder(requireContext(), R.style.DialogStyle)
            .setView(dialogView)
            .setPositiveButton("확인") { _, _ ->
                val newTitle = editText.text.toString()
                onTitleUpdated(newTitle)
            }
            .setNegativeButton("취소", null)

        return builder.create()
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setDimAmount(0.7f) // 어둡게 정도
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }
}
