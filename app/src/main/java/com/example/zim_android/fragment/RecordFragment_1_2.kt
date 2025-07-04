package com.example.zim_android.fragment

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.zim_android.R
import com.example.zim_android.Record_2_1_Activity
import com.example.zim_android.databinding.Record12FragmentBinding
import org.w3c.dom.Text

class RecordFragment_1_2: Fragment(R.layout.record_1_2_fragment){

    private var _binding: Record12FragmentBinding? = null
    private val binding get() = _binding!!

    private var selectedThemeIndex = 1
    private var selectedThemeName: String? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = Record12FragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.commonHeader.tvTitle.text = "기록하기"

        // root에 포커스 해제 설정
        binding.root.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                clearFocusAndHideKeyboard()
            }
            false
        }

        // 테마 선택
        binding.themeLayout1.setOnClickListener {
            updateThemeSelection(1)
            clearFocusAndHideKeyboard() }
        binding.themeLayout2.setOnClickListener {
            updateThemeSelection(2)
            clearFocusAndHideKeyboard() }
        binding.themeLayout3.setOnClickListener {
            updateThemeSelection(3)
            clearFocusAndHideKeyboard() }

        // 이전 화면으로 돌아가기
        binding.backBtn.setOnClickListener {
            findNavController().navigate(R.id.action_recordFragment_1_2_to_recordFragment_1_1)
        }

        // 다음 버튼 클릭
        binding.saveBtn.setOnClickListener {
            val intent = Intent(requireContext(), Record_2_1_Activity::class.java)
            intent.putExtra("trip_name", binding.tripTitle.text) // 여행명
            intent.putExtra("trip_description", binding.tripDescription.text) // 여행설명
            intent.putExtra("selected_theme", selectedThemeName) // 테마명
            startActivity(intent)
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    // 여행 테마 설정 이미지 바꾸기
    fun updateThemeSelection(index: Int) {
        selectedThemeIndex = index
        when (index) {
            1 -> selectedThemeName = "basic_theme"
            2 -> selectedThemeName = "summer_theme"
            3 -> selectedThemeName = "winter_theme"
        }

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

    // 포커스 헤재하는 함수
    private fun clearFocusAndHideKeyboard() {
        val focused = requireActivity().currentFocus
        focused?.clearFocus()
        val imm = requireContext().getSystemService(InputMethodManager::class.java)
        imm.hideSoftInputFromWindow(focused?.windowToken, 0)
    }
}
