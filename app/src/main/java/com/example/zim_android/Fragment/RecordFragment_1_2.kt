package com.example.zim_android.Fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.zim_android.R
import com.example.zim_android.databinding.Record12FragmentBinding

class RecordFragment_1_2: Fragment(R.layout.record_1_2_fragment){

    private var _binding: Record12FragmentBinding? = null
    private val binding get() = _binding!!

    private var selectedThemeIndex = 1


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = Record12FragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.commonHeader.tvTitle.text = "기록하기"

        binding.themeLayout1.setOnClickListener { updateThemeSelection(1) }
        binding.themeLayout2.setOnClickListener { updateThemeSelection(2) }
        binding.themeLayout3.setOnClickListener { updateThemeSelection(3) }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    // 여행 테마 설정 이미지 바꾸기
    fun updateThemeSelection(index: Int) {
        selectedThemeIndex = index

        binding.themeBtn1.setImageResource(
            if (index == 1) R.drawable.selected_rectangle else R.drawable.unselected_rectangle
        )
        binding.themeBtn2.setImageResource(
            if (index == 2) R.drawable.selected_rectangle else R.drawable.unselected_rectangle
        )
        binding.themeBtn3.setImageResource(
            if (index == 3) R.drawable.selected_rectangle else R.drawable.unselected_rectangle
        )
    }
}
