package com.example.zim_android.View

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import com.example.zim_android.R
import android.util.Log
import com.example.zim_android.MainActivity
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.user.UserApiClient
import com.kakao.sdk.common.model.ClientError
import com.kakao.sdk.common.model.ClientErrorCause
import com.example.zim_android.data.model.KakaoLoginRequest
import com.example.zim_android.data.network.ApiProvider.api
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import com.example.zim_android.data.model.KakaoLoginResponse


// ReOnboardingActivity.kt
class ReOnboardingActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_re_onboarding)

        val kakaoButton = findViewById<ImageButton>(R.id.kakao_login_button)

        kakaoButton.setOnClickListener {
            val callback: (OAuthToken?, Throwable?) -> Unit = { token, error ->
                if (error != null) {
                    Log.e("카카오 로그인", "로그인 실패", error)
                } else if (token != null) {
                    Log.d("카카오 로그인", "토큰 받음: ${token.accessToken}")

                    // accessToken 서버에 전송
                    val request = KakaoLoginRequest(token.accessToken)
                    api.kakaoLogin(request).enqueue(object : Callback<KakaoLoginResponse> {
                        override fun onResponse(call: Call<KakaoLoginResponse>, response: Response<KakaoLoginResponse>) {
                            if (response.isSuccessful) {
                                val loginResult = response.body()
                                if (loginResult?.registered == true) {
                                    // 가입된 유저면 Main으로
                                    startActivity(Intent(this@ReOnboardingActivity, MainActivity::class.java))
                                } else {
                                    // 미가입자는 온보딩 5페이지로
                                    val intent = Intent(this@ReOnboardingActivity, OnBoardingActivity::class.java)
                                    intent.putExtra("page", 4)
                                    startActivity(intent)
                                }
                                finish()
                            } else {
                                Log.e("카카오 서버 로그인 실패", "code=${response.code()}")
                            }
                        }

                        override fun onFailure(call: Call<KakaoLoginResponse>, t: Throwable) {
                            Log.e("카카오 서버 로그인 실패", t.message ?: "Unknown error")
                        }
                    })
                }
            }

            // 로그인 시도
            if (UserApiClient.instance.isKakaoTalkLoginAvailable(this)) {
                UserApiClient.instance.loginWithKakaoTalk(this, callback = callback)
            } else {
                UserApiClient.instance.loginWithKakaoAccount(this, callback = callback)
            }
        }

    }
}
