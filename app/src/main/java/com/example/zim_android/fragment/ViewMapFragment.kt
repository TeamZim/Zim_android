package com.example.zim_android.fragment

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import com.example.zim_android.R
import com.example.zim_android.databinding.ViewMapDialog1Binding
import com.example.zim_android.databinding.ViewMapDialog2Binding
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
            showAddRecordDialog1()
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    private fun showAddRecordDialog1() {
        val dialog1 = Dialog(requireContext())
        val dialog1Binding = ViewMapDialog1Binding.inflate(layoutInflater)
        dialog1.setContentView(dialog1Binding.root)

        // x버튼 클릭시 다이얼로그 내리기
        dialog1Binding.dialog1ExitBtn.setOnClickListener {
            dialog1.dismiss()
        }

        // 감정색 선택 버튼 클릭
        dialog1Binding.selectColorBtn.setOnClickListener {
            // 두 번째 다이얼로그 가지고 오기
            val dialog2 = Dialog(requireContext())
            val dialog2Binding = ViewMapDialog2Binding.inflate(layoutInflater)
            dialog2.setContentView(dialog2Binding.root)

            // < 버튼 클릭시 다이얼로그 내리고 dialog_1 올리기
            dialog2Binding.backToDialog1.setOnClickListener {
                dialog2.dismiss()
                // showAddRecordDialog1()
            }

            dialog2.window?.setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            dialog2.window?.setBackgroundDrawableResource(android.R.color.transparent)

            dialog2.show()
        }

        // 임시 더미 리스트
        val countryNames = listOf("Korea", "한국", "일본", "미국", "프랑스", "독일", "중국", "영국", "이탈리아", "스페인", "러시아", "브라질", "캐나다", "멕시코", "사우디아라비아", "태국", "인도", "베트남", "싱가포르", "남아프리카공화국", "스웨덴", "호주", "네덜란드", "뉴질랜드", "노르웨이", "핀란드", "스위스", "포르투갈", "폴란드", "덴마크", "아르헨티나", "칠레", "이집트", "터키", "아랍에미리트", "인도네시아")
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, countryNames)
        dialog1Binding.dialog1CountryListTextInput.setAdapter(adapter)

        val selectedCountry = dialog1Binding.dialog1CountryListTextInput.text.toString() // 사용자가 선택한 국가명 텍스트로 저장


        dialog1.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        dialog1.window?.setBackgroundDrawableResource(android.R.color.transparent)

        dialog1.show()
    }
}