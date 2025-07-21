package com.example.zim_android.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.window.Dialog
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.zim_android.R
import com.example.zim_android.SplashActivity
import com.example.zim_android.View.OnBoardingActivity
import com.example.zim_android.View.ReOnboardingActivity
import com.example.zim_android.data.model.MyPageResponse
import com.example.zim_android.data.network.ApiProvider.api
import com.example.zim_android.data.network.UserSession
import com.example.zim_android.databinding.DeleteAccountDialogBinding
import com.example.zim_android.databinding.MypageDialog1Binding
import com.example.zim_android.databinding.SettingsFragmentBinding
import com.kakao.sdk.user.UserApiClient
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SettingsFragment: Fragment(R.layout.settings_fragment){

    // 뷰바인딩 사용
    private var _binding: SettingsFragmentBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = SettingsFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val userId = UserSession.userId ?: 8
        Log.d("userId", userId.toString())
        binding.commonHeader.tvTitle.text = "설정"
        binding.commonHeader.exitBtn.visibility = View.VISIBLE

        binding.commonHeader.exitBtn.setOnClickListener {
            findNavController().navigate(R.id.action_settingsFragment_to_mypageFragment)
        }

        // 로그아웃
        binding.logoutText.setOnClickListener {
            Log.d("Logout", "🟢 로그아웃 텍스트 클릭됨") // 이 로그가 나오는지 먼저 확인
            api.logout(userId).enqueue(object : Callback<ResponseBody> {
                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                    if (response.isSuccessful) {
                        val message = response.body()?.string()
                        Log.d("Logout", "로그아웃 성공 - 응답 메시지: $message")

                        // 🔁 온보딩 화면으로 이동
                        val intent = Intent(requireContext(), ReOnboardingActivity::class.java)
                        intent.putExtra("page", 0) // 0번 페이지 = 첫 화면
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)

                    } else {
                        Log.e("Logout", "서버 오류: ${response.code()}")
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    Log.e("Logout", "네트워크 오류: ${t.message}")
                }
            })

        }


        // 계정 삭제
        binding.deleteAccountText.setOnClickListener {
            val deleteAccountDialog = android.app.Dialog(requireContext())
            val dialogBinding = DeleteAccountDialogBinding.inflate(layoutInflater) // 뷰를 코드로 가지고와서 이제 객체를 얘를 통해 받아오면됨.
            deleteAccountDialog.setContentView(dialogBinding.root) // 다이얼로그의 UI를 XML과 연결

            // 취소
            dialogBinding.cancelBtnImg.setOnClickListener {
                deleteAccountDialog.dismiss()
            }

            // 확인
            dialogBinding.confirmBtnImg.setOnClickListener {
                api.deleteAccount(userId).enqueue(object : Callback<ResponseBody> {
                    override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                        if (response.isSuccessful) {
                            val message = response.body()?.string()
                            Log.d("DeleteAccount", "계정 삭제 성공 - 응답 메시지: $message")

                            // ✅ 카카오 연결 끊기
                            UserApiClient.instance.unlink { error ->
                                if (error != null) {
                                    Log.e("카카오 연결 끊기 실패", error.toString())
                                } else {
                                    Log.i("카카오 연결 끊기", "성공적으로 연결 해제됨")
                                }
                            }

                            val intent = Intent(requireContext(), SplashActivity::class.java)
                            intent.putExtra("page", 0)
                            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            startActivity(intent)
                        } else {
                            Log.e("DeleteAccount", "계정 삭제 실패 - 응답 코드: ${response.code()}")
                            val errorBody = response.errorBody()?.string()
                            Log.e("DeleteAccount", "에러 내용: $errorBody")
                        }
                    }

                    override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                        Log.e("DeleteAccount", "통신 실패: ${t.message}")
                    }
                })
            }


            deleteAccountDialog.show()
        }

//        // 개인 정보 수정
//        binding.modifyInfoBtn.setOnClickListener {
//            // TODO("나중에 온보딩 완성된 후 해당 페이지로 연결")
//        }


    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
