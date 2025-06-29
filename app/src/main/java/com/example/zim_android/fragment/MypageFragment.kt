package com.example.zim_android.fragment

import DialogMypage1Adapter
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.zim_android.R
import com.example.zim_android.data.model.CountryData
import com.example.zim_android.data.model.CountryItem
import com.example.zim_android.databinding.MypageDialog1Binding
import com.example.zim_android.databinding.MypageFragmentBinding

class MypageFragment: Fragment(R.layout.mypage_fragment){

    // 뷰바인딩 사용
    private var _binding: MypageFragmentBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = MypageFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.commonHeader.tvTitle.text = "마이페이지"
        binding.commonHeader.settingsBtn.visibility = View.VISIBLE
        binding.commonHeader.exitBtn.visibility = View.GONE

        binding.commonHeader.settingsBtn.setOnClickListener {
            findNavController().navigate(R.id.action_mypageFragment_to_settingsFragment)
        }

        binding.visitedCountryCountLayer.setOnClickListener {
            showCountryListDialog()
        }

        // 이모지 넣을 텍스트뷰 가져오기
        // val imageContainer = view.findViewById<TextView>(R.id.country_flag_text_1)
        // 백엔드에서 방문한 나라 리스트 받아와 텍스트에 개수에 따라 추가하는 로직 추가해야함.

        // 백엔드에서 리스트 받아오기

        // 리스트의 길이에 따라 조건 나누기
        // 10개 이하인 경우
        // flag_image_container1 에 넣기
        // imageView3 의 길이 조절

        // 10개 이상인 경우
        // flag_image_container2에 넣기
        // imageView3의 길이 조절

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }



    private fun showCountryListDialog() {
        val dialog = Dialog(requireContext()) // 커스텀 다이얼로그 객체 생성
        val dialogBinding = MypageDialog1Binding.inflate(layoutInflater) // 뷰를 코드로 가지고와서 이제 객체를 얘를 통해 받아오면됨.
        dialog.setContentView(dialogBinding.root) // 다이얼로그의 UI를 XML과 연결

        dialogBinding.dialog1ExitBtn.setOnClickListener {
            dialog.dismiss()
        }

        // 그리드에 들어갈 더미 데이터 예시
        val countryitems = CountryData.countryList

        val adapter = DialogMypage1Adapter(requireContext(), countryitems) // gridview 어댑터를 인스턴스화
        dialogBinding.countryListGridview.adapter = adapter// 다이얼로그 XML 안의 GridView에 어댑터를 연결
        // 그리드가 화면에 아이템들을 렌더링하게 됨

        // 다이얼로그 속성 설정 (크기 등)
        dialog.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,  // 가로 사이즈
            ViewGroup.LayoutParams.WRAP_CONTENT   // 세로 사이즈
        )
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent) // 다이얼로그 뒷 배경 처리

        dialog.show()
    }

}
