package com.example.zim_android.fragment

import DialogMypage1Adapter
import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.zim_android.R
import com.example.zim_android.data.model.CountryData
import com.example.zim_android.data.model.MyPageResponse
import com.example.zim_android.data.network.ApiProvider.api
import com.example.zim_android.data.network.UserSession
import com.example.zim_android.databinding.MypageDialog1Binding
import com.example.zim_android.databinding.MypageFragmentBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

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
        binding.commonHeader.settingBtn.visibility = View.VISIBLE

        binding.commonHeader.settingBtn.setOnClickListener {
            findNavController().navigate(R.id.action_mypageFragment_to_settingsFragment)
        }

        binding.visitedCountryCountLayer.setOnClickListener {
            showCountryListDialog()
        }

        val userId = UserSession.userId ?: 1

        api.getMypage(userId).enqueue(object : Callback<MyPageResponse> {
            override fun onResponse(call: Call<MyPageResponse>, response: Response<MyPageResponse>) {
                if (response.isSuccessful) {
                    val myPageData = response.body()
                    myPageData?.let {
                        Glide.with(requireContext()).load(it.user.profileImageUrl).into(binding.mypageProfileImage)

                        Log.d("myPageData", it.user.surName)

                        binding.surnameTitle.text = it.user.surName
                        binding.firstNameText.text = it.user.firstName
                        binding.koreanNameText.text = it.user.koreanName
                        binding.birthText.text = it.user.birth
                        binding.nationalityText.text = it.user.nationality

                        binding.visitedCountryCount.text = it.statistics.countryCount.toString()
                        binding.diaryCount.text = it.statistics.diaryCount.toString()

                        // 유니코드 국기 이모지를 2글자 단위로 분리
                        val flagList = it.flags.chunked(2)  // 국기 하나 = 2 유니코드 문자

                        if (it.flags.length <= 10) {
                            binding.countryFlagLine1.text = it.flags
                            binding.countryFlagLine2.visibility = View.GONE

                            val params = binding.countryFlagLine1.layoutParams as ConstraintLayout.LayoutParams
                            params.bottomMargin = (32 * binding.root.context.resources.displayMetrics.density).toInt()
                            params.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID
                            binding.countryFlagLine1.layoutParams = params
                        } else {
                            // 앞 10개 → line1 / 나머지 → line2
                            val firstLine = flagList.take(10).joinToString("")
                            val secondLine = flagList.drop(10).joinToString("")

                            binding.countryFlagLine1.text = firstLine
                            binding.countryFlagLine2.text = secondLine
                        }
                    }
                } else {
                    Log.e(
                        "MYPAGE",
                        "응답 실패 - code: ${response.code()}, errorBody: ${response.errorBody()?.string()}"
                    )
                    // Log.e("MYPAGE", "응답 실패: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<MyPageResponse>, t: Throwable) {
                Log.e("MYPAGE", "통신 실패: ${t.message}")
            }
        })
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