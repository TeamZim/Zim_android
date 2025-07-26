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
import com.example.zim_android.data.model.VisitedCountryResponse
import com.example.zim_android.data.network.ApiProvider.api
import com.example.zim_android.data.network.UserSession
import com.example.zim_android.databinding.MypageDialog1Binding
import com.example.zim_android.databinding.MypageFragmentBinding
import com.example.zim_android.fragment.ViewMapFragment.VisitedCountryListResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MypageFragment: Fragment(R.layout.mypage_fragment){

    private var _binding: MypageFragmentBinding? = null
    private val binding get() = _binding!!

    private val userId = UserSession.userId ?: 8

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

        Log.d("마이페이지", userId.toString())

        // 시작 시: 로딩은 보이고, 콘텐츠는 숨김
        binding.progressBar.visibility = View.VISIBLE
        binding.contentLayout.visibility = View.GONE


        api.getMypage(userId).enqueue(object : Callback<MyPageResponse> {
            override fun onResponse(call: Call<MyPageResponse>, response: Response<MyPageResponse>) {

                binding.progressBar.visibility = View.GONE
                binding.contentLayout.visibility = View.VISIBLE

                if (response.isSuccessful) {
                    val myPageData = response.body()
                    myPageData?.let {
                        Glide.with(requireContext()).load(it.user.profileImageUrl).centerCrop().into(binding.mypageProfileImage)
                        Log.d("이미지 URL", it.user.profileImageUrl)

                        Log.d("myPageData", it.user.surName)

                        binding.surnameText.text = it.user.surName
                        binding.firstNameText.text = it.user.firstName
                        binding.koreanNameText.text = it.user.koreanName
                        binding.birthText.text = it.user.birth
                        binding.nationalityText.text = it.user.nationality

                        binding.passportTextLine1.text = "${binding.surnameText.text}<<${binding.firstNameText.text}<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<"
                        binding.passportTextLine2.text = "1103V572811M132K0312V200000000000000ME-MORY"

                        binding.visitedCountryCount.text = it.statistics.countryCount.toString()
                        binding.diaryCount.text = it.statistics.diaryCount.toString()

                        // 유니코드 국기 이모지를 2글자 단위로 분리
                        val flagList = it.flags.chunked(2)  // 국기 하나 = 2 유니코드 문자

                        if (flagList.size <= 10) {
                            binding.countryFlagLine1.text = it.flags
                        } else {
                            binding.countryFlagLine2.visibility = View.VISIBLE

                            val params = binding.countryFlagLine1.layoutParams as ConstraintLayout.LayoutParams
                            params.bottomMargin = 0
                            params.bottomToBottom = ConstraintLayout.LayoutParams.UNSET
                            binding.countryFlagLine1.layoutParams = params

                            // 앞 18개 → line1 / 나머지 → line2
                            val trimmedList = flagList.take(72)
                            val firstLine = trimmedList.take(36).joinToString("")
                            val secondLine = trimmedList.drop(36).take(36).joinToString("")


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
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }



    private fun showCountryListDialog() {
        val dialog = Dialog(requireContext())
        val dialogBinding = MypageDialog1Binding.inflate(layoutInflater)
        dialog.setContentView(dialogBinding.root)

        dialogBinding.dialog1ExitBtn.setOnClickListener {
            dialog.dismiss()
        }

        api.getVisitedCountries(userId).enqueue(object : Callback<VisitedCountryListResponse> {
            override fun onResponse(
                call: Call<VisitedCountryListResponse>,
                response: Response<VisitedCountryListResponse>
            ) {
                if (response.isSuccessful) {
                    val countryList = response.body()?.data ?: emptyList()

                    val adapter = DialogMypage1Adapter(requireContext(), countryList)
                    dialogBinding.countryListGridview.adapter = adapter

                    dialogBinding.dialog1Title.text = "${binding.koreanNameText.text}님이 방문한 나라"
                    dialogBinding.countryCount.text = "${countryList.size}개국"

                    if (countryList.size >= 19) {
                        dialogBinding.gradientImg.visibility = View.VISIBLE
                    }

                    dialog.window?.setLayout(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                    )
                    dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

                    dialog.show()
                } else {
                    Log.e("VisitedCountry", "❗ 응답 실패: ${response.code()}, ${response.message()}")
                }
            }

            override fun onFailure(call: Call<VisitedCountryListResponse>, t: Throwable) {
                Log.e("VisitedCountry", "❌ 네트워크 실패: ${t.message}")
            }
        })


    }

}