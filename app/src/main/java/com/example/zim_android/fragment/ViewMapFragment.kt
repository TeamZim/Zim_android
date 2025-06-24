package com.example.zim_android.fragment

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.core.content.ContextCompat
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

        dialogBinding.selectColorBtn.setOnClickListener {
            // 색깔 선택 다이얼로그로 이동
        }

        val popupBg = dialogBinding.countrListAutoComplete.dropDownBackground
        Log.d("Dropdown", "Background: $popupBg") // null이면 테마 적용 안 된 것

        // 임시 더미 리스트
        val countryNames = listOf("Korea", "한국", "일본", "미국", "프랑스", "독일", "중국", "영국", "이탈리아", "스페인", "러시아", "브라질", "캐나다", "멕시코", "사우디아라비아", "태국", "인도", "베트남", "싱가포르", "남아프리카공화국", "스웨덴", "호주", "네덜란드", "뉴질랜드", "노르웨이", "핀란드", "스위스", "포르투갈", "폴란드", "덴마크", "아르헨티나", "칠레", "이집트", "터키", "아랍에미리트", "인도네시아")
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, countryNames)
        dialogBinding.countrListAutoComplete.setAdapter(adapter)

        val selectedCountry = dialogBinding.countrListAutoComplete.text.toString() // 사용자가 선택한 국가명 텍스트로 저장




        dialog.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        dialog.show()
    }

}