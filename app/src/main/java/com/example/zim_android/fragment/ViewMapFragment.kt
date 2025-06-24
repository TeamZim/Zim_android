package com.example.zim_android.fragment

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.zim_android.R
import com.example.zim_android.databinding.ViewMapDialog1Binding
import com.example.zim_android.databinding.ViewMapFragmentBinding

class ViewMapFragment : Fragment(R.layout.view_map_fragment) {

    private var _binding: ViewMapFragmentBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = ViewMapFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.commonHeader.tvTitle.text = "지도 모음"
        binding.addRecordBtn.setOnClickListener {
            showAddRecordDialog()
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    private fun showAddRecordDialog() {
        val dialog = Dialog(requireContext())
        val dialogBinding = ViewMapDialog1Binding.inflate(layoutInflater)
        dialog.setContentView(dialogBinding.root)

        // x버튼 클릭시 다이얼로그 내리기
        dialogBinding.dialogExitBtn.setOnClickListener {
            dialog.dismiss()
        }

        dialog.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        dialog.show()
    }

}